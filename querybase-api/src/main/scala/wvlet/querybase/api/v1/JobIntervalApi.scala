package wvlet.querybase.api.v1

import wvlet.airframe.http.*
import wvlet.querybase.api.interval.{CapacitySimulatorReport, ClusterCapacity, JobInterval}
import wvlet.querybase.api.v1.JobIntervalApi.*

@RPC
trait JobIntervalApi {
  def getIntervals(request: TargetTimeRange): GetIntervalResponse
  def getSimulationResult(request: SimulationRequest): CapacitySimulatorReport[JobInterval]
}

object JobIntervalApi extends RxRouterProvider {
  override def router = RxRouter.of[JobIntervalApi]

  case class TargetTimeRange(start: Long, end: Long)
  case class GetIntervalResponse(intervals: Seq[JobInterval])

  case class SimulationRequest(timeRange: TargetTimeRange, capacity: ClusterCapacity)
}
