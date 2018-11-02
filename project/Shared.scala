import com.github.sbt.cpd.CpdKeys.cpdSkipDuplicateFiles
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType, _}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import webscalajs.ScalaJSWeb

object Shared {
  val projectId = "boilerplay"
  val projectName = "Boilerplay"
  val projectPort = 9000

  object Versions {
    val app = "1.0.0"
    val scala = "2.12.7"
  }

  val compileOptions = Seq(
    "-target:jvm-1.8", "-encoding", "UTF-8", "-feature", "-deprecation", "-explaintypes", "-feature", "-unchecked",
    "–Xcheck-null", "-Xfatal-warnings", /* "-Xlint", */ "-Xcheckinit", "-Xfuture", "-Yrangepos", "-Ypartial-unification",
    "-Yno-adapted-args", /*"-Ywarn-dead-code",*/ "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-numeric-widen", "-Ywarn-infer-any"
  )

  lazy val commonSettings = Seq(
    version := Shared.Versions.app,
    scalaVersion := Shared.Versions.scala,

    scalacOptions ++= compileOptions,
    scalacOptions in (Compile, console) ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Xfatal-warnings"))),
    scalacOptions in (Compile, doc) := Seq("-encoding", "UTF-8"),

    publishMavenStyle := false,

    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false).withWarnDirectEvictions(false),

    cpdSkipDuplicateFiles := true,

    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", _ @ _*) => MergeStrategy.first
      case PathList("javax", "xml", _ @ _*) => MergeStrategy.first
      case PathList(p @ _*) if p.last.contains("about_jetty-") => MergeStrategy.discard
      case PathList("org", "apache", "commons", "logging", _ @ _*) => MergeStrategy.first
      case PathList("org", "w3c", "dom", _ @ _*) => MergeStrategy.first
      case PathList("org", "w3c", "dom", "events", _ @ _*) => MergeStrategy.first
      case PathList("javax", "annotation", _ @ _*) => MergeStrategy.first
      case PathList("net", "jcip", "annotations", _ @ _*) => MergeStrategy.first
      case PathList("play", "api", "libs", "ws", _ @ _*) => MergeStrategy.first
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
      case PathList("sqlj", _ @ _*) => MergeStrategy.first
      case PathList("play", "reference-overrides.conf") => MergeStrategy.first
      case "module-info.class" => MergeStrategy.discard
      case "messages" => MergeStrategy.concat
      case "pom.xml" => MergeStrategy.discard
      case "JS_DEPENDENCIES" => MergeStrategy.discard
      case "pom.properties" => MergeStrategy.discard
      case "application.conf" => MergeStrategy.concat
      case x => (assemblyMergeStrategy in assembly).value(x)
    }
  )

  lazy val shared = (crossProject(JSPlatform, JVMPlatform)
    .withoutSuffixFor(JVMPlatform)
    .crossType(CrossType.Pure) in file("shared"))
    .settings(
    commonSettings: _*
  ).settings(
    (sourceGenerators in Compile) += ProjectVersion.writeConfig(projectId, projectName, projectPort).taskValue,
    libraryDependencies ++= Dependencies.Serialization.circeProjects.map(c => "io.circe" %%% c % Dependencies.Serialization.circeVersion) ++ Seq(
      "com.beachape" %%% "enumeratum-circe" % Dependencies.Utils.enumeratumCirceVersion,
      "me.chrons" %%% "boopickle" % Dependencies.Utils.booPickleVersion,Dependencies.Utils.fastparser,
      "io.github.cquiroz" %%% "scala-java-time" % Dependencies.Utils.javaTimeVersion
    )
  ).jvmSettings(libraryDependencies += Dependencies.ScalaJS.jvmStubs)

  lazy val sharedJs = shared.js.enablePlugins(ScalaJSWeb)

  lazy val sharedJvm = shared.jvm
}
