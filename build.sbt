// It will automatically reload build.sbt changes at sbt console
Global / onChangedBuildSource := ReloadOnSourceChanges

val SCALA_3          = "3.3.3"
val AIRFRAME_VERSION = sys.env.getOrElse("AIRFRAME_VERSION", "23.6.1")
val AIRSPEC_VERSION  = "23.6.1"

ThisBuild / scalaVersion := SCALA_3

val buildSettings = Seq[Setting[_]](
  organization       := "org.wvlet",
  description        := "Analyze big data analytics metrics and provide product insight",
  crossPaths         := true,
  publishMavenStyle  := true,
  Test / logBuffered := false,
  // Use AirSpec for unit testing https://wvlet.org/airframe/docs/airspec
  libraryDependencies ++= Seq(
    "org.wvlet.airframe" %%% "airspec" % AIRSPEC_VERSION % Test
  ),
  testFrameworks += new TestFramework("wvlet.airspec.Framework")
)

lazy val querybase =
  project
    .in(file("."))
    .settings(
      buildSettings,
      name        := "querybase",
      description := "querybase root project",
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %% "airframe-launcher" % AIRFRAME_VERSION
      )
    )
    .dependsOn(server)
    .aggregate(api.jvm, api.js, client.jvm, client.js, server)

lazy val api =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("querybase-api"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildSettings,
      name             := "querybase-api",
      description      := "api project shared between server(JVM) and UI (Scala.js)",
      buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "wvlet.querybase",
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %%% "airframe-http"    % AIRFRAME_VERSION,
        "org.wvlet.airframe" %%% "airframe-control" % AIRFRAME_VERSION,
        "org.wvlet.airframe" %%% "airframe-codec"   % AIRFRAME_VERSION,
        "org.wvlet.airframe" %%% "airframe-log"     % AIRFRAME_VERSION
      )
    )

lazy val client = {
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("querybase-client"))
    .enablePlugins(AirframeHttpPlugin)
    .settings(
      buildSettings,
      name                := "querybase-client",
      description         := "querybase RPC client",
      airframeHttpClients := Seq("wvlet.querybase.api.v1:rpc"),
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %%% "airframe-http" % AIRFRAME_VERSION
      )
    )
    .dependsOn(api)
}

lazy val server =
  project
    .in(file("querybase-server"))
    .settings(
      buildSettings,
      name        := "querybase-server",
      description := "RPC server for querybase",
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %% "airframe-http-netty" % AIRFRAME_VERSION,
        "org.duckdb"          % "duckdb_jdbc"         % "0.9.1"
      )
    ).dependsOn(api.jvm, client.jvm)

val publicDev  = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")

import org.scalajs.linker.interface.{StandardConfig, OutputPatterns}
import org.scalajs.linker.interface.{ModuleKind, ModuleSplitStyle}

lazy val ui =
  project
    .enablePlugins(ScalaJSPlugin)
    .in(file("querybase-ui"))
    .settings(
      buildSettings,
      name        := "querybase-ui",
      description := "querybase UI",
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %%% "airframe-http" % AIRFRAME_VERSION,
        // For rendering DOM
        "org.wvlet.airframe" %%% "airframe-rx-html" % AIRFRAME_VERSION
      ),
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= {
        linkerConfig(_)
      },
      publicDev  := s"target/scala-${scalaVersion.value}/querybase-ui-fastopt",
      publicProd := s"target/scala-${scalaVersion.value}/querybase-ui-opt",
      externalNpm := {
        import java.nio.file.{Files, Paths}
        val yarnProg = Files.exists(Paths.get("/opt/homebrew/bin/yarn")) match {
          // Workaround for IntelliJ sbt project loader on Mac OS X
          case true  => "/opt/homebrew/bin/yarn"
          case false => "yarn"
        }
        // Use Yarn instead of npm
        scala.sys.process.Process(List(yarnProg, "--silent"), baseDirectory.value).!
        baseDirectory.value
      }
    )
    .dependsOn(api.js, client.js)

def linkerConfig(config: StandardConfig): StandardConfig = {
  config
    // Workaround for the error:
    // Surfaces.scala(235:12:Return): java.io.Serializable expected but java.lang.Class found for tree of type org.scalajs.ir.Trees$Apply
    .withCheckIR(false)
    .withSourceMap(true)
    .withModuleKind(ModuleKind.ESModule)
    .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("wvlet.querybase.ui")))
}
