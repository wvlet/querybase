addSbtPlugin("com.eed3si9n"  % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.5.0")

// For RPC client
addSbtPlugin("org.wvlet.airframe" % "sbt-airframe" % "23.6.1")

// Settings for Scala.js
val SCALAJS_VERSION = sys.env.getOrElse("SCALAJS_VERSION", "1.13.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % SCALAJS_VERSION)

// For building the project for Scala and Scala.js
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.1")
