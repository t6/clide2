### @service services:ContextMenu ###
define [], () -> ($timeout) ->  
  state = { 
    current: null     
  }
  el = null
  state.display = (create, x, y) ->    
    el = document.getElementById('contextmenu') unless el?
    el.style.left = x
    el.style.top = y
    state.current = create
    el.focus()
  return state