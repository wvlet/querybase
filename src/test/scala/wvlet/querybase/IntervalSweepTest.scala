package wvlet.querybase

import wvlet.airspec.AirSpec
import wvlet.querybase.interval.{IntervalSweep, JobInterval}

class IntervalSweepTest extends AirSpec {
  test("sweep") {
    val sortedIntervals = Seq(
      JobInterval("job 1", 1, 2, 5, "n/a", 20, 20),
      JobInterval("job 2", 2, 2, 3, "n/a", 50, 50)
    )
      .sortBy(_.created_time)

    IntervalSweep.sweep[JobInterval](
      sortedIntervals.iterator,
      reporter = { report =>
        info(s"sweep line at ${report.sweepLine}, ${report.overlappedIntervals}")
      }
    )
  }
}
