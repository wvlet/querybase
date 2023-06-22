package wvlet.querybase.ui

import org.scalajs.dom
import wvlet.airframe.rx.html.{DOMRenderer, RxElement}

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
  def render = div(
    "Hello Querybase!!!!"
  )
}
