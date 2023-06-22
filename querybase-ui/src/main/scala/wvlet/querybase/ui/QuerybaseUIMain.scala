package wvlet.querybase.ui

import org.scalajs.dom
import wvlet.airframe.rx.html.{DOMRenderer, RxElement}
import wvlet.querybase.api.v1.ServiceRPC
import wvlet.airframe.http.*
import wvlet.querybase.api.v1.JobIntervalApi.TargetTimeRange

object QuerybaseUIMain {
  def main(args: Array[String]): Unit = {
    render
  }
  def render: Unit = {
    val mainNode = dom.document.getElementById("main") match {
      case null =>
        val elem = dom.document.createElement("div")
        elem.setAttribute("id", "main")
        dom.document.body.appendChild(elem)
      case other => other
    }

    DOMRenderer.renderTo(mainNode, QuerybaseUI())
  }
}

import wvlet.airframe.rx.html.*
import wvlet.airframe.rx.html.all.*

class QuerybaseUI extends RxElement {
  private val rpcClient = ServiceRPC.newRPCAsyncClient(Http.client.newJSClient)

  def render = div(
    cls -> "container",
    div(
      cls -> "text-primary",
      "Hello Querybase!!!",
      table(
        rpcClient.JobIntervalApi.getIntervals(TargetTimeRange(1, 1000)).map { result =>
          result.intervals.map { x =>
            tr(
              td(x.toString)
            )
          }
        }
      )
    )
  )
}
