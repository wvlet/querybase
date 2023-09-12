addSbtPlugin("com.eed3si9n"  % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.5.2")

// For RPC client
addSbtPlugin("org.wvlet.airframe" % "sbt-airframe" % "23.7.3")

// Settings for Scala.js
val SCALAJS_VERSION = sys.env.getOrElse("SCALAJS_VERSION", "1.13.2")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % SCALAJS_VERSION)

// For building the project for Scala and Scala.js
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

// For converting JS modules into Scala.js interface
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta42")

// For reloading server upon code change
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")
