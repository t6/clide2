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
##   it under the terms of the GNU General Public License as published by     ##
##   the Free Software Foundation, either version 3 of the License, or        ##
##   (at your option) any later version.                                      ##
##                                                                            ##
##   Clide is distributed in the hope that it will be useful,                 ##
##   but WITHOUT ANY WARRANTY; without even the implied warranty of           ##
##   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            ##
##   GNU General Public License for more details.                             ##
##                                                                            ##
##   You should have received a copy of the GNU General Public License        ##
##   along with Clide.  If not, see <http://www.gnu.org/licenses/>.           ##
##                                                                            ##

### @service services:Session ###
define ['routes','collaboration/Operation','collaboration/CodeMirror','collaboration/Client','collaboration/Annotations','modes/isabelle/defaultWords','codemirror'], (routes,Operation,CMAdapter,Client,Annotations,idw,CodeMirror) -> ($q,$rootScope,$http,Toasts) ->
  pc = routes.clide.web.controllers.Projects

  queue = []
  socket  = undefined

  session =
    state: 'closed'
    collaborators: null
    openFiles: null
    talkback: null
    chat: []
    me: null

  session.activeDoc = ->
    session.openFiles?[session.me.activeFile]?.doc

  session.activeAnnotations = ->
    session.openFiles?[session.me.activeFile]?.annotations

  session.syncState = ->
    session.openFiles?[session.me.activeFile]?.$syncState()

  apply = (f) -> unless $rootScope.$$phase then $rootScope.$apply(f) else f()

  initFile = (file) ->
    nfile = session.openFiles[file.info.id] or { }

    nfile.id   = file.info.id
    nfile.name = file.info.name
    if file.info.mimeType is 'text/isabelle'
      nfile.doc  = CodeMirror.Doc file.state,
        name: 'isabelle'
        words: idw
    else
      nfile.doc = CodeMirror.Doc file.state, (file.info.mimeType or 'text/plain')

    client  = new Client(file.revision)
    adapter = new CMAdapter(nfile.doc, session.me.color)

    client.applyOperation = adapter.applyOperation
    client.sendOperation = (rev,op) -> send
        f: nfile.id # TODO: handle on server!
        r: rev
        o: op.actions
    client.sendAnnotation = (rev,an,name) -> send
        f: nfile.id
        r: rev
        a: an.annotations
        n: name

    adapter.registerCallbacks
      change: (op) -> client.applyClient(op)
      annotate: (a) -> client.annotate(a)

    nfile.$ackEdit = () -> client.serverAckEdit()
    nfile.$ackAnnotation = () -> client.serverAckAnnotation()
    nfile.$apply = (os) -> client.applyServer(os)
    nfile.$syncState = -> client.syncState()
    nfile.$setColor = (c) -> adapter.setColor(c)
    nfile.$annotate = (a,u,n) -> # TODO: include user
      adapter.applyAnnotation(a,u,n)
      a                          = client.transformAnnotation(a)
      nfile.annotations          = nfile.annotations or { }
      nfile.annotations[u.id]    = nfile.annotations[u.id] or [ ]
      for stream in nfile.annotations[u.id]
        if stream.name is n
          return
      nfile.annotations[u.id].push
        show: true
        name: n

    session.openFiles[file.info.id] = (nfile)

  getOpenFile = (id) -> session.openFiles[id] or false

  remove = (id) ->
    for s, i in session.collaborators
      if s.id is id
        return session.collaborators.splice(i,1)

  update = (info) ->
    for s, i in session.collaborators
      if s.id is info.id
        for k, v of info
          session.collaborators[i][k] = v
        session.collaborators[i].activeFile = info.activeFile
        return true
    session.collaborators.push(info)

  getUser = (id) ->
    for s in session.collaborators
      if s.id is id
        return s
    return null

  get = (username, project, init) ->
    ws = new WebSocket(pc.session(username,project).webSocketURL())
    queue.push(JSON.stringify(init)) if init?
    socket = ws
    apply ->
      session.state = 'connecting'
    ws.onmessage = (e) ->
      console.log "received: #{e.data}"
      msg = JSON.parse(e.data)
      switch typeof msg
        when 'number'
          f = getOpenFile(msg)
          if f
            f.$ackEdit()
          else
            log.warning("acknowledge for unknown file " + msg)
        when 'object'
          if msg.f? and msg.o?
            getOpenFile(msg.f).$apply(Operation.fromJSON(msg.o))
          else if msg.f? and msg.a? and (user = getUser(msg.u))?
            apply ->
              getOpenFile(msg.f).$annotate(Annotations.fromJSON(msg.a),user,msg.n)
          switch msg.t
            when 'e'
              Toasts.push 'danger', msg.c
            when 'welcome'
              session.openFiles = { }
              #document.getElementById('theme').href = "/client/stylesheets/colors/#{msg.info.color}.css"
              apply ->
                session.me = msg.info
                session.collaborators = msg.others
                session.chat = msg.chat.reverse()
            when 'opened'
              apply -> initFile(msg.c)
            when 'failed'
              Toasts.push("danger","the initialization of the requested file failed on the server")
            when 'talk'
              apply ->
                session.talkback?(msg.c)
                session.chat.unshift(msg.c)
            when 'close'
              apply ->
                delete session.openFiles[msg.c]
                session.me.activeFile = null
            when 'switch'
              apply ->
                session.me.activeFile = msg.c
            when 'session_changed'
              apply ->
                update(msg.c)
            when 'session_stopped'
              apply ->
                remove(msg.c.id)
    ws.onopen = (e) ->
      apply -> session.state = 'connected'
      for msg in queue
        ws.send(msg)
      queue = []
      CodeMirror.registerHelper "hint", (e...) ->
        return (
          showHint: () -> console.log 'hn'
        )
    ws.onclose = ws.onerror = (e) ->
      socket = undefined
      session.collaborators = null
      session.openFiles = null
      session.me.activeFile = null
      session.me = null
      session.chat = []
      apply -> session.state = 'disconnected'

  send = (message) -> switch socket?.readyState
    when WebSocket.CONNECTING
      queue.push(JSON.stringify(message))
    when WebSocket.OPEN
      data = JSON.stringify(message)
      socket.send(data)

  return (
    getOpenFile: getOpenFile
    info: session
    init: (username, project, init) ->
      socket or get(username, project, init)
      send
        t: 'init'
    openFile: (id) -> unless session.me.activeFile is id
      send
        t: 'open'
        id: id
    closeFile: (id) ->
      send
        t: 'close'
        id: id
    chat: (msg) ->
      send
        t:   'chat'
        msg: msg
    invite: (name) ->
      send
        t: 'invite'
        u: name
    setColor: (color) ->
      session.me.color = color
      for key, file of session.openFiles
        file.$setColor?(color)

      #document.getElementById('theme').href = "/client/stylesheets/colors/#{color}.css"
      send
        t: 'color'
        c: color
    hints: (cm, options) ->

    close: ->
      queue = []
      socket?.close()
  )
