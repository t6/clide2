import sbt._
import Keys._
import play.Project._
import Dependencies._

object ApplicationBuild extends Build {
  val appName         = "clide"
  val appVersion      = "2.0-SNAPSHOT"

  override def rootProject = Some(web)

  val coreDependencies = Seq(akka.actor,akka.remote,slick)  

  val core = Project(s"${appName}-core", file("modules/clide-core"))
             .settings(
    scalaVersion := scala.version,
    libraryDependencies ++= coreDependencies
  )

  val appDependencies = Seq(akka.testkit,akka.remote,
    "com.typesafe"       %% "play-plugins-mailer" % "2.1.0",
    "com.typesafe.play"  %% "play-slick"          % "0.5.0.2")

  val web = play.Project(
    appName, 
    appVersion, 
    appDependencies,
    path = file("modules/clide-web")
  ).settings(Angular.defaultSettings:_*).settings(
    scalaVersion := scala.version,
    resolvers += Resolver.url("github repo for play-slick",
      url("https://raw.github.com/loicdescotte/loicdescotte.github.com/master/releases/"))
      (Resolver.ivyStylePatterns),
    requireJs += "main.js",
    requireJsShim += "main.js",
    Angular.otherModules ++= Map(
        "angular-animate"  -> "ngAnimate",
        "angular-cookies"  -> "ngCookies",
        "angular-route"    -> "ngRoute",
        "angular-resource" -> "ngResource",
        "ui-bootstrap"     -> "ui.bootstrap"),
    Angular.moduleDirs ++= Map(
        "controllers" -> ("controller", "Controller", true),
        "directives"  -> ("directive","",false),
        "filters"     -> ("filter","",false),
        "services"    -> ("service","",true)),
    resourceGenerators in Compile <+= LessCompiler,
    resourceGenerators in Compile <+= JavascriptCompiler(fullCompilerOptions = None),
    resourceGenerators in Compile <+= Angular.ModuleCompiler,
    resourceGenerators in Compile <+= Angular.BoilerplateGenerator,
    lessEntryPoints         <<= (sourceDirectory in Compile){ base => 
      base / "assets" / "stylesheets" * "main.less" },
    coffeescriptEntryPoints <<= (sourceDirectory in Compile){ base => 
      base / "assets" ** "*.coffee" },
    javascriptEntryPoints <<= (sourceDirectory in Compile){ base => 
      (base / "assets" ** "*.js") --- 
      (base / "assets" / "libs" / "bootstrap" / "assets" ** "*") --- 
      (base / "assets" / "libs" / "bootstrap" / "js" / "tests" ** "*") --- 
      (base / "assets" / "libs" / "codemirror" / "test" ** "*") }
  )

  val isabelleDependencies = Seq(akka.actor,akka.remote,scala.swing,scala.actors)    

  val isabelle = Project(s"${appName}-isabelle", file("modules/clide-isabelle"))
                .dependsOn(web).settings(
    scalaVersion := scala.version,
    libraryDependencies ++= isabelleDependencies
  )
}
