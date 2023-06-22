package wvlet.querybase.server.api

import wvlet.log.LogSupport
import wvlet.querybase.api.v1.JobIntervalApi
import wvlet.querybase.api.interval.JobInterval
import wvlet.querybase.interval.{CapacitySimulator, JobIntervalUtil}
import wvlet.querybase.api.interval.CapacitySimulatorReport

class JobIntervalApiImpl extends JobIntervalApi with LogSupport {
  override def getIntervals(request: JobIntervalApi.TargetTimeRange): JobIntervalApi.GetIntervalResponse = {
    info(request)

    JobIntervalApi.GetIntervalResponse(
      intervals = Seq(
        JobInterval(
          name = "job 1",
          created_time = 1,
          start_time = 10,
          finished_time = 100,
          sig = "n/a",
          cpu = 1000,
          memory = 1.0
        )
      )
    )
  }

  override def getSimulationResult(request: JobIntervalApi.SimulationRequest): CapacitySimulatorReport[JobInterval] = {
    val jobs             = JobIntervalUtil.loadFromParquet("data/sample_jobs.parquet")
    val simulationReport = CapacitySimulator.simulateJobSchedule(jobs, request.capacity)
    simulationReport
  }
}
