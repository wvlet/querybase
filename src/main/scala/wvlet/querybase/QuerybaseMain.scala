package wvlet.querybase

import wvlet.airframe.launcher.*
import wvlet.log.LogSupport
import wvlet.querybase.server.QuerybaseServer

object QuerybaseMain {
  def main(args: Array[String]): Unit = {
    Launcher.of[QuerybaseMain].execute(args)
  }
}

class QuerybaseMain(
    @option(prefix = "-h,--help", description = "show help messages", isHelp = true)
    isHelp: Boolean = false
) extends LogSupport {

  @command(description = "Show the version", isDefault = true)
  def showVersion(): Unit = {
    info(s"querybase version: ${BuildInfo.version}")
  }

  @command(description = "Run a query simulation")
  def simulate(
      @argument(description = "input file")
      inputFile: String
  ): Unit = {
    info(s"Simulate jobs in ${inputFile}")
  }

  @command(description = "Run a query simulation")
  def server(
      @option(prefix = "-p,--port", description = "server port")
      port: Int = 8080
  ): Unit = {
    info(s"Launching a querybase server at port ${port}")
    QuerybaseServer.startServer(port = port)
  }
}
