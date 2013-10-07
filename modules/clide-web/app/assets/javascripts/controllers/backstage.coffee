### @controller clide.controllers:BackstageController ###
define ['util/md5'], (md5) -> ($scope, $location, $routeParams, $timeout, Projects, Console, Auth, Toasts, Dialog) ->  
  $scope.user = $routeParams.user

  unless Auth.loggedIn
    $location.path '/login'
    Toasts.push 'warning', 'You need to log in to view the requested resource!'
    return

  Auth.validateSession 
    success: -> 
      if $scope.user isnt Auth.user?.username
        $location.path '/login'
        Toasts.push 'warning', 'The requested resource is not associated with your user account!'
      else
        $scope.email = Auth.user.email
        $scope.gravatar = md5(Auth.user.email)
    error: -> 
      $location.path '/login'
      Toasts.push 'warning', 'Sorry, your login session has expired! Please enter your credentials once again.'
  
  $scope.selectedProject = null

  $scope.selectProject = (project) ->     
    $scope.selectedProject = project

  Projects.get($scope.user).then (projects) ->
    console.log projects
    $scope.projects = projects.userProjects
    $scope.otherProjects = projects.collaborating

  $scope.projectContextMenu = (project) ->
    [
      icon: 'trash'
      text: "delete '#{project.name}'"
      action: -> $scope.deleteProject(project)
    ]

  $scope.createProject = (name,description,error) -> Dialog.push
    error: error
    title: 'new project'
    queries: [
      { text: 'Name:', name: 'name', value: name }
      { text: 'Short description:', name: 'description', type: 'textarea', value: description } 
    ]
    buttons: ['Ok','Cancel']
    done: (answer,result) -> if answer is 'Ok'
      Projects.put($scope.user,result).then (project) -> 
        $scope.projects.push(project) 

  $scope.deleteProject = (project) -> Dialog.push
    title: "Delete project"    
    text:  "Do you really want to delete project '#{project.name}'? " +
           "This can not be undone!"
    buttons: ['Yes','No']
    done: (answer) -> if answer is 'Yes'
      Projects.delete($scope.user,project).then () ->
        i = $scope.projects.indexOf(project)
        if i >= 0
          $scope.projects.splice(i,1)
          if $scope.selectedProject is project
            $scope.selectedProject = null

  $scope.start = () ->
    $location.path "/#{$scope.selectedProject.owner}/#{$scope.selectedProject.name}/"