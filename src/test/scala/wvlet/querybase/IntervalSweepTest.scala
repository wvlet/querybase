package wvlet.querybase

import wvlet.airspec.AirSpec

class IntervalSweepTest extends AirSpec {
  test("sweep") {
    val sortedIntervals = Seq(
      JobInterval("job 1", 1, 5),
      JobInterval("job 2", 2, 3)
    )
      .sortBy(_.start)

    IntervalSweep.sweep[JobInterval](
      sortedIntervals.iterator,
      reporter = { report =>
        info(s"sweep line at ${report.sweepLine}, ${report.overlappedIntervals}")
      }
    )
  }

}
