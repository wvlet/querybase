// It will automatically reload build.sbt changes at sbt console
Global / onChangedBuildSource := ReloadOnSourceChanges

val SCALA_3          = "3.3.0"
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
    "org.wvlet.airframe" %% "airspec" % AIRSPEC_VERSION % Test
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
        "org.wvlet.airframe" %% "airframe-launcher" % AIRFRAME_VERSION,
        "org.duckdb"          % "duckdb_jdbc"       % "0.8.1"
      )
    )
    .dependsOn(api.jvm)

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
        "org.wvlet.airframe" %%% "airframe-control" % AIRFRAME_VERSION,
        "org.wvlet.airframe" %%% "airframe-codec"   % AIRFRAME_VERSION,
        "org.wvlet.airframe" %%% "airframe-log"     % AIRFRAME_VERSION
      )
    )

// TODO add server for RPC service

