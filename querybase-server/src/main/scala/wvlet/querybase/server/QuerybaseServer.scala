package wvlet.querybase.server

import wvlet.airframe.http.RxRouter
import wvlet.airframe.http.netty.Netty
import wvlet.querybase.server.api.JobIntervalApiImpl

object QuerybaseServer {

  def router = RxRouter.of[JobIntervalApiImpl]
  def design = Netty.server.withRouter(router).design

  def startServer(port: Int) = {
    Netty.server
      .withPort(port)
      .withRouter(router)
      .start { server =>
        server.awaitTermination()
      }
  }
}
