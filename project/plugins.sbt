scalacOptions ++= Seq( "-unchecked", "-deprecation" )

resolvers += Resolver.typesafeRepo("releases")

resolvers += Resolver.url("jetbrains-bintray", url("http://dl.bintray.com/jetbrains/sbt-plugins/"))(Resolver.ivyStylePatterns)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")

// SBT-Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.3")

libraryDependencies += "org.webjars.npm" % "source-map" % "0.7.3"

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

// Scala.js
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.5.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.25")
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.8-0.6" exclude("org.scala-js", "sbt-scalajs"))

//addSbtPlugin("org.scala-js"              % "sbt-scalajs"               % "1.0.0-M5")
//addSbtPlugin("com.vmunier"               % "sbt-web-scalajs"           % "1.0.8") 

// Source Control
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

// App Packaging
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.12")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")

// Dependency Resolution
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.1.0-M4")

// Code Quality
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0") // scalastyle

addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.7")

// Disabled until semanticdb is updated
// addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.10" exclude("io.get-coursier", "coursier-cache_2.12") exclude("io.get-coursier", "coursier_2.12"))

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.1") // scalariformFormat

addSbtPlugin("com.github.sbt" % "sbt-cpd" % "2.0.0")

// Utilities
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2") // dependencyGraph

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4") // dependencyUpdates

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.7") // stats

// addSbtPlugin("pl.project13.sbt" % "sbt-jol" % "0.1.3") // jol:internals

// addSbtPlugin("com.github.jozic" % "sbt-about-plugins" % "0.1.0") // about-plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-license-report" % "1.2.0")

// Visualization
addSbtPlugin("com.dwijnand" % "sbt-project-graph" % "0.4.0") // projectsGraphDot

addSbtPlugin("com.github.xuwei-k" % "sbt-class-diagram" % "0.2.1") // classDiagram

// Documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.4.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.2" exclude("com.lightbend.paradox", "sbt-paradox"))

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")

// IDE Integration
addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.1.2")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")
