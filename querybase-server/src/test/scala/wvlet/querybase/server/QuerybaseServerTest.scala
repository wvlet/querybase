package wvlet.querybase.server

import wvlet.airframe.Design
import wvlet.airframe.http.{Http, HttpStatus}
import wvlet.airframe.http.netty.NettyServer
import wvlet.airspec.AirSpec
import wvlet.querybase.api.v1.JobIntervalApi.GetIntervalRequest
import wvlet.querybase.api.v1.ServiceRPC

class QuerybaseServerTest extends AirSpec {

  protected override def design: Design = {
    QuerybaseServer.design
  }

  test("start server") { (server: NettyServer) =>
    // TODO access the server
    debug(server.localAddress)
    debug(QuerybaseServer.router)

    val client = Http.client.newSyncClient(server.localAddress)

    test("successful response") {
      val response = client.sendSafe(
        Http
          .POST("/wvlet.querybase.api.v1.JobIntervalApi/getIntervals")
          .withJson("""{"request": {"start":2, "end":10}}""")
      )
      response.status shouldBe HttpStatus.Ok_200
      // response.message shoudlBe ...
    }

    test("invalid response test") {
      val response = client.sendSafe(
        Http
          .POST("/wvlet.querybase.api.v1.JobIntervalApi/getIntervals")
          .withJson("""{"requ": {"start":2, "end":10}}""")
      )

      response.status shouldBe HttpStatus.BadRequest_400
    }

  }

  test("access server with RPC client") { (server: NettyServer) =>
    val rpcClient = ServiceRPC.newRPCSyncClient(Http.client.newSyncClient(server.localAddress))
    val response  = rpcClient.JobIntervalApi.getIntervals(GetIntervalRequest(5, 8))
    info(response)
  }
}
