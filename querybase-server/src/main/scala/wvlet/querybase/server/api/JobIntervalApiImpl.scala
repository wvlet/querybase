package wvlet.querybase.server.api

import wvlet.log.LogSupport
import wvlet.querybase.api.v1.JobIntervalApi
import wvlet.querybase.api.interval.JobInterval

class JobIntervalApiImpl extends JobIntervalApi with LogSupport {
  override def getIntervals(request: JobIntervalApi.GetIntervalRequest): JobIntervalApi.GetIntervalResponse = {
    info(request)

    JobIntervalApi.GetIntervalResponse(
      intervals = Seq(
        JobInterval(
          name = "job 1",
          created_time = 1,
          start_time = 10,
          finished_time = 20,
          sig = "n/a",
          cpu = 1000,
          memory = 1.0
        )
      )
    )
  }
}
