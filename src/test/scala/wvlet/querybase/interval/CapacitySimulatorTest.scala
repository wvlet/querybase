package wvlet.querybase.interval

import wvlet.airspec.AirSpec
import wvlet.querybase.interval.{CapacitySimulator, JobInterval}

class CapacitySimulatorTest extends AirSpec {
  test("simulateJobSchedule") {
    val sortedIntervals = Seq(
      JobInterval("job 1", 1, 3, 10, "n/a", 20, 20),
      JobInterval("job 2", 2, 2, 3, "n/a", 50, 50)
    )
      .sortBy(_.created_time)
    val capacity = CapacitySimulator.ClusterCapacity(1, 1000000, 1000000)
    val result = CapacitySimulator.simulateJobSchedule[JobInterval](
      jobs = sortedIntervals,
      capacity = capacity
    )
    debug(result)
    result shouldBe CapacitySimulator.CapacitySimulatorReport(
      List(JobInterval("job 1",1,1,8,"n/a",20,20), JobInterval("job 2",2,8,9,"n/a",50,50)),
      capacity
    )
  }
}