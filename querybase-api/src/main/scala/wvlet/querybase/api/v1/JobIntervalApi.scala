package wvlet.querybase.api.v1

import wvlet.querybase.api.interval.JobInterval
import wvlet.querybase.api.v1.JobIntervalApi.*

trait JobIntervalApi {
  def getIntervals(request: GetIntervalRequest): GetIntervalResponse
}

object JobIntervalApi {
  case class GetIntervalRequest(start: Long, end: Long)
  case class GetIntervalResponse(intervals: Seq[JobInterval])
}
