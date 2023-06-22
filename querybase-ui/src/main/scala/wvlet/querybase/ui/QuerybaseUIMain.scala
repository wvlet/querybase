package wvlet.querybase.ui

import org.scalajs.dom
import wvlet.airframe.rx.html.{DOMRenderer, RxElement}
import wvlet.querybase.api.v1.ServiceRPC
import wvlet.airframe.http.*
import wvlet.airframe.rx.Rx
import wvlet.log.LogSupport
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
import wvlet.airframe.rx.html.svgTags.*
import wvlet.airframe.rx.html.svgAttrs.*

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

class TimeRangeSelector extends RxElement with LogSupport {

  private val selectedRange = Rx.variable("1")

  def getSelectedTimeRange: Rx[String] = selectedRange

  override def render = select(
    cls -> "form-select",
    onchange -> { (e: dom.Event) =>
      info(s"onchange: ${e.currentTarget}")
      e.currentTarget match {
        case e: dom.HTMLSelectElement =>
          info(s"selected value: ${e.value}")
          selectedRange := e.value
        case _ =>
      }
    },
    option(value -> "1", "1 hour"),
    option(value -> "3", "3 hours"),
    option(value -> "6", "6 hours"),
    option(value -> "12", "12 hours"),
    option(value -> "24", "24 hours"),
    option(value -> "48", "48 hours"),
    option(value -> "72", "72 hours"),
    option(value -> "168", "1 week"),
    option(value -> "336", "2 weeks"),
    option(value -> "720", "1 month"),
    option(value -> "1440", "2 months"),
    option(value -> "2160", "3 months"),
    option(value -> "4320", "6 months"),
    option(value -> "8760", "1 year")
  )
}

class QuerybaseUI extends RxElement with LogSupport {
  private val rpcClient = ServiceRPC.newRPCAsyncClient(Http.client.newJSClient)

  private val timeRangeSelector = TimeRangeSelector()
  override def render = div(
    NavBar(),
    table(
      tr(
        td(
          timeRangeSelector
        ),
        td(
          timeRangeSelector.getSelectedTimeRange.map { x =>
            span(s"selected time range value: ${x}")
          }
        )
      ),
      tr(
        td(
          svgTags.svg(
            svgAttrs.width  -> 1000,
            svgAttrs.height -> 200,
            svgTags.rect(
              svgAttrs.x      -> 0,
              svgAttrs.y      -> 0,
              svgAttrs.width  -> 1000,
              svgAttrs.height -> 200,
              svgAttrs.fill   -> "#EEEEEE"
            ),
            timeRangeSelector.getSelectedTimeRange.flatMap { x =>
              rpcClient.JobIntervalApi.getIntervals(TargetTimeRange(1, x.toInt * 3600)).map { result =>
                info(result)
                result.intervals.map { x =>
                  svgTags.rect(
                    svgAttrs.x      -> 1,
                    svgAttrs.y      -> 1,
                    svgAttrs.width  -> 100,
                    svgAttrs.height -> 20,
                    fill            -> "#FF8888"
                  )
                }
              }
            }
          )
        )
      )
    )
  )
}
