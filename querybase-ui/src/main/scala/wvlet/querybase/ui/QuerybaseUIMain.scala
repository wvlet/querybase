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
import wvlet.airframe.rx.html.{svgTags, svgAttrs}

class NavBar extends RxElement {
  override def render: RxElement =
    nav(
      cls -> "navbar bg-dark border-bottom bottom-dark",
      a(
        cls  -> "navbar-brand text-light mx-3",
        href -> "#",
        "Querybase"
      )
    )
}

class QuerybaseUI extends RxElement {
  private val rpcClient = ServiceRPC.newRPCAsyncClient(Http.client.newJSClient)

  override def render = div(
    div(
      NavBar(),
      svgTags.svg(
        svgTags.rect(
          svgAttrs.x      -> 0,
          svgAttrs.y      -> 0,
          svgAttrs.width  -> 1800,
          svgAttrs.height -> 200,
          svgAttrs.fill   -> "#EEEEEE"
        ),
        rpcClient.JobIntervalApi.getIntervals(TargetTimeRange(1, 1000)).map { result =>
          result.intervals.map { x =>
            svgTags.rect(
              svgAttrs.x      -> x.created_time,
              svgAttrs.y      -> 0,
              svgAttrs.width  -> (x.finished_time - x.created_time).toInt,
              svgAttrs.height -> 20,
              svgAttrs.fill   -> "pink"
            )
          }
        }
      )
    )
  )
}
