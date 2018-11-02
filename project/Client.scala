import org.scalajs.sbtplugin.ScalaJSPlugin
import webscalajs.ScalaJSWeb
import sbt.Keys._
import sbt._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Client {
  private[this] val clientSettings = Shared.commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % Dependencies.ScalaJS.jQueryVersion,
      "com.lihaoyi" %%% "scalatags" % Dependencies.Utils.scalatagsVersion,
      "com.thoughtworks.binding" %%% "dom" % Dependencies.Utils.bindingScalaVersion,
      "com.thoughtworks.binding" %%% "route" % Dependencies.Utils.bindingScalaVersion,
      "com.thoughtworks.binding" %%% "binding" % Dependencies.Utils.bindingScalaVersion,
      "com.thoughtworks.binding" %%% "futurebinding" % Dependencies.Utils.bindingScalaVersion,
      "com.thoughtworks.binding" %%% "jspromisebinding" % Dependencies.Utils.bindingScalaVersion,
      "org.scala-lang.modules" %% "scala-xml" % Dependencies.Utils.scalaxmlVersion,
      Dependencies.Utils.scribe
    )
  )

  lazy val client = (project in file("client")).settings(clientSettings: _*
    ).settings(addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
    .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
    .dependsOn(Shared.sharedJs)
}
