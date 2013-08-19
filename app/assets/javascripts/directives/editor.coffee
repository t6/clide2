### @directive directives:editor ###
define ['codemirror','routes','concurrency/TextOperation','concurrency/CodeMirrorAdapter','concurrency/Client'], (CodeMirror,routes,TextOperation,CodeMirrorAdapter,Client) -> () -> 
  restrict: 'E'
  transclude: true
  controller: ($scope, $element) ->
    null
  template: '<textarea ng-transclude></textarea>'
  replace: true
  link: (scope, iElem, iAttrs, controller) ->    
    cm = CodeMirror.fromTextArea iElem[0],
      lineNumbers: true
      readOnly: true
      undoDepth: 0 # disable

    socket = new WebSocket(routes.controllers.Application.collab().webSocketURL())

    client = null

    socket.onmessage = (e) -> 
      msg = JSON.parse(e.data)
      switch msg.type
        when 'init'
          console.log 'initialized'
          cm.setValue(msg.doc)          
          client = new Client(msg.rev)
          adapter = new CodeMirrorAdapter(cm)
          client.sendOperation = (revision, operation) ->
            socket.send JSON.stringify
              type: 'change'
              rev: revision
              op: operation
          client.applyOperation = adapter.applyOperation
          adapter.registerCallbacks            
            change: (op) -> client.applyClient(op)
          cm.setOption 'readOnly', false
        when 'ack'
          client.serverAck()
        when 'change'
          client.applyServer(msg.rev, TextOperation.fromJSON(msg.op))
        when 'error'
          console.log msg.msg
          console.log msg.ss

    socket.onopen = ->
      socket.send JSON.stringify
        type: 'register'

    socket.onclose = ->
      console.log 'connection lost'