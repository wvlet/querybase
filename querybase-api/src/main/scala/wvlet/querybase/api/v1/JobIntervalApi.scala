package wvlet.querybase.api.v1

import wvlet.airframe.http.*
import wvlet.querybase.api.interval.JobInterval
import wvlet.querybase.api.v1.JobIntervalApi.*

@RPC
trait JobIntervalApi {
  def getIntervals(request: GetIntervalRequest): GetIntervalResponse
}

object JobIntervalApi extends RxRouterProvider {
  override def router = RxRouter.of[JobIntervalApi]

  case class GetIntervalRequest(start: Long, end: Long)
  case class GetIntervalResponse(intervals: Seq[JobInterval])
}
