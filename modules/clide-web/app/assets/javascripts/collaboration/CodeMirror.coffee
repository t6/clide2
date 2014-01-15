##             _ _     _                                                      ##
##            | (_)   | |                                                     ##
##         ___| |_  __| | ___      clide 2                                    ##
##        / __| | |/ _` |/ _ \     (c) 2012-2013 Martin Ring                  ##
##       | (__| | | (_| |  __/     http://clide.flatmap.net                   ##
##        \___|_|_|\__,_|\___|                                                ##
##                                                                            ##
##   This file is part of Clide.                                              ##
##                                                                            ##
##   Clide is free software: you can redistribute it and/or modify            ##
##   it under the terms of the GNU Lesser General Public License as           ##
##   published by the Free Software Foundation, either version 3 of           ##
##   the License, or (at your option) any later version.                      ##
##                                                                            ##
##   Clide is distributed in the hope that it will be useful,                 ##
##   but WITHOUT ANY WARRANTY; without even the implied warranty of           ##
##   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            ##
##   GNU General Public License for more details.                             ##
##                                                                            ##
##   You should have received a copy of the GNU Lesser General Public         ##
##   License along with Clide.                                                ##
##   If not, see <http://www.gnu.org/licenses/>.                              ##
##                                                                            ##

define ['collaboration/Operation','collaboration/Annotations','codemirror'], (Operation,Annotations,CodeMirror) ->
  CodeMirror.commands.getHelp = (cm) ->
    doc = cm.getDoc()
    if doc? and !doc.somethingSelected()
      CodeMirror.signal(doc,'getHelp',doc,doc.indexFromPos(doc.getCursor()))
  CodeMirror.keyMap['default']['Ctrl-Space'] = 'getHelp'

  class CodeMirrorAdapter
    constructor: (@doc,@color) ->
      @doc.on "beforeChange", @onChange
      @doc.on "beforeSelectionChange", @onSelectionChange
      @doc.on "getHelp", @onGetHelp
      @annotations = {}

    setColor: (color) =>
      @color = color
      @doc.setSelection(@doc.getCursor('anchor'),@doc.getCursor('head'))

    # Removes all event listeners from the CodeMirror instance.
    detach: =>
      @doc.off "beforeChange", @onChange

    # Converts a CodeMirror change object into a Operation
    @operationFromCodeMirrorChange: (change, doc) ->
      from = doc.indexFromPos(change.from)
      to   = doc.indexFromPos(change.to)
      length = to - from
      new Operation().retain(from)
                         .delete(length)
                         .insert(change.text.join('\n') )
                         .retain(doc.getValue().length-to) # could be cached

    # Apply an operation to a CodeMirror instance.
    @applyOperationToCodeMirror: (operation, doc) ->
      index = 0 # TODO: Iterate Line/Column based
      for a in operation.actions
        switch Operation.actionType(a)
          when 'retain'
            index += a
          when 'insert'
            doc.replaceRange a, doc.posFromIndex(index)
            index += a.length
          when 'delete'
            from = doc.posFromIndex(index)
            to = doc.posFromIndex(index - a)
            doc.replaceRange "", from, to

    @annotationFromCodeMirrorSelection: (doc,selection,color) ->
      anchor = doc.indexFromPos(selection.anchor)
      head   = doc.indexFromPos(selection.head)

      length = doc.getValue().length # TODO: see above

      if anchor is head
        return new Annotations().plain(anchor)
                                .annotate(0,{'c':['cursor',color]})
                                .plain(length - anchor)
      else if anchor < head
        return new Annotations().plain(anchor)
                                .annotate(head - anchor,{'c':['selection',color]})
                                .annotate(0,{'c':['cursor',color]})
                                .plain(length - head)
      else
        return new Annotations().plain(head)
                                .annotate(0,{'c':['cursor',color]})
                                .annotate(anchor - head,{'c':['selection', color]})
                                .plain(length - anchor)

    registerCallbacks: (cb) =>
      @callbacks = cb

    onChange: (doc, change) =>
      unless @silent
        @trigger "change", CodeMirrorAdapter.operationFromCodeMirrorChange(change, doc)

    onSelectionChange: (doc, change) =>
      unless @silent
        @trigger "annotate", CodeMirrorAdapter.annotationFromCodeMirrorSelection(doc, change, @color)

    onGetHelp: (doc, index) =>
      key = Math.random().toString(36).substr(2)
      annotation = new Annotations().plain(index).annotate(0,{'c': ['cursor',@color],'h': [key]}).plain(doc.getValue().length - index)
      @trigger 'annotate', annotation

    getValue: => @doc.getValue()

    trigger: (event,args...) =>
      action = @callbacks and @callbacks[event]
      action.apply this, args  if action

    applyOperation: (operation) =>
      @silent = true
      cm = @doc.getEditor()
      if cm? then cm.operation =>
        CodeMirrorAdapter.applyOperationToCodeMirror operation, @doc
      else
        CodeMirrorAdapter.applyOperationToCodeMirror operation, @doc
      @silent = false

    annotate: (c,user,name,from,to) =>
      classes = c.c?.join(' ')
      widget = (type,cs,content) ->
        w = angular.element("<#{type} class='#{classes} #{cs}'>#{content}</#{type}>")[0]
        w.title = c.t if c.t
        return w
      line = if to? then to.line else from.line
      if c.e? and cm = @doc.getEditor()
        for e in c.e
          @annotations[user.id][name].push cm.addLineWidget line, widget('div','outputWidget error',e)
      if c.w? and cm = @doc.getEditor()
        for w in c.w
          @annotations[user.id][name].push cm.addLineWidget line, widget('div','outputWidget warning',w)
      if c.i? and cm = @doc.getEditor()
        for i in c.i
          @annotations[user.id][name].push cm.addLineWidget line, widget('div','outputWidget info',i)
      if c.o? and cm = @doc.getEditor()
        for i in c.o
          @annotations[user.id][name].push cm.addLineWidget line, widget('div','outputWidget info',i)
      if classes?
        if to? and c.s?
          marker = @doc.markText from, to,
            replacedWith:      widget('span',classes,c.s)
            handleMouseEvents: true
          @annotations[user.id][name].push marker
        else if to?
          marker = @doc.markText from, to,
            className: classes
            title:     c.t
          @annotations[user.id][name].push marker
        else
          bookmark = @doc.setBookmark from,
            widget:     widget('span','','')
            insertLeft: true
          @annotations[user.id][name].push bookmark

    @registerMouseEvents: (doc) =>

    resetAnnotations: (user, name) => if user?
      unless @annotations[user]?
        @annotations[user] = {}
      existing = @annotations[user][name]
      if existing?
        for marker in existing
          marker.clear()
      @annotations[user][name] = []

    applyAnnotation: (annotation, user, name) =>
      cm       = @doc.getEditor()

      work = =>
        @resetAnnotations(user.id, name) if user?

        index = 0 # TODO: Iterate Line/Column based with cm.eachLine
        for a in annotation.annotations
          if Annotations.isPlain(a)
            index += a
          else
            from = @doc.posFromIndex(index)
            if a.l > 0
              index += a.l
              to = @doc.posFromIndex(index)
              @annotate(a.c,user,name,from,to)
            else
               @annotate(a.c,user,name,from)

      if cm? then cm.operation => work()
      else work()
