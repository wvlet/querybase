// It will automatically reload build.sbt changes at sbt console
Global / onChangedBuildSource := ReloadOnSourceChanges

val SCALA_3          = "3.3.0"
val AIRFRAME_VERSION = sys.env.getOrElse("AIRFRAME_VERSION", "23.6.0")
val AIRSPEC_VERSION  = "23.6.0"

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
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildSettings,
      name             := "querybase",
      description      := "querybase root project",
      buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "wvlet.querybase",
      libraryDependencies ++= Seq(
        "org.wvlet.airframe" %% "airframe-launcher" % AIRFRAME_VERSION,
        "org.wvlet.airframe" %% "airframe-log"      % AIRFRAME_VERSION
      )
    )
