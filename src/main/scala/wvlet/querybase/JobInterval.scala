package wvlet.querybase

import wvlet.airframe.codec.MessageCodec
import wvlet.log.LogSupport

import java.io.File

case class JobInterval(name: String, override val start: Long, override val end: Long) extends Interval(start, end) {
  override def toString: String = s"${name}:[${start}, ${end})"
}

object JobInterval extends LogSupport {
  def loadFromJson(jsonFilePath: String): Seq[JobInterval] = {
    // read the json file and map to JobInterval by using airframe-codec
    val jsonData = wvlet.airframe.control.IO.readAsString(new File(jsonFilePath))
    debug(jsonData)
    val jobList = MessageCodec.of[Seq[JobInterval]].fromJson(jsonData)
    jobList
  }
}
