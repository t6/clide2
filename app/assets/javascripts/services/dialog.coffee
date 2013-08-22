### @service services:Dialog ###
define [], () -> () ->
  console.log 'initializing dialog service'
  
  queue = []

  state = {}
  
  next = (action) ->
    if state.show
      result = {}
      for q in state.queries
        result[q.name] = q.value
      action(result)
    if queue.length > 0            
      config = queue.shift()
      state.show = true
      state.title = config.title
      state.message = config.message
      state.buttons = config.buttons
      state.queries = config.queries
    else
      state.show = false
      state.title = null
      state.message = null
      state.buttons = []
      state.queries = []

  create = (config) ->    
    queue.unshift config
    next()  

  state.create = create
  state.next = next

  return state