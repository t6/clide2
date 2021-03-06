logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("scala-js-releases",
    url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.play" % "sbt-plugin"      % "2.2.1")

addSbtPlugin("com.typesafe.akka" % "akka-sbt-plugin" % "2.2.3")

addSbtPlugin("com.typesafe.sbt"  % "sbt-atmos"       % "0.3.1")

addSbtPlugin("com.typesafe.sbt"  % "sbt-atmos-play"  % "0.3.1")

addSbtPlugin("com.typesafe.sbt"  % "sbt-pgp"         % "0.8.1")

addSbtPlugin("org.scala-lang.modules.scalajs" % "scalajs-sbt-plugin" % "0.2.1")
