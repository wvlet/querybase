package wvlet.querybase.interval

import wvlet.airspec.AirSpec
import wvlet.querybase.api.interval.{CapacitySimulatorReport, ClusterCapacity, JobInterval}

class CapacitySimulatorTest extends AirSpec {
  test("simulateJobSchedule") {
    val sortedIntervals = Seq(
      JobInterval("job 1", 1, 3, 10, "n/a", 20, 20),
      JobInterval("job 2", 2, 2, 3, "n/a", 50, 50)
    )
      .sortBy(_.created_time)
    val capacity = ClusterCapacity(1, 1000000, 1000000)
    val result = CapacitySimulator.simulateJobSchedule[JobInterval](
      jobs = sortedIntervals,
      capacity = capacity
    )
    debug(result)
    result shouldBe CapacitySimulatorReport(
      List(JobInterval("job 1", 1, 1, 8, "n/a", 20, 20), JobInterval("job 2", 2, 8, 9, "n/a", 50, 50)),
      capacity
    )
  }

  test("simulate 579 jobs") {
    val jobs = JobIntervalUtil.loadFromParquet("data/jobs_579.parquet")
    for (maxConcurrentJobs <- Seq(1, 10, 100); cpu <- Seq(70000, 700000, 7000000); memory <- Seq(2, 4, 8, 16)) {
      val capacity = ClusterCapacity(maxConcurrentJobs, cpu, memory)
      val result = CapacitySimulator.simulateJobSchedule[JobInterval](
        jobs = jobs,
        capacity = capacity
      )
      val totalQueuedTime = result.simulatedJobs.map(j => j.start_time - j.created_time).sum
      // Get the 95-tile of queued time
      val queuedTime95 = result.simulatedJobs
        .map(j => j.start_time - j.created_time).sorted.apply((result.simulatedJobs.size * 0.95).toInt)

      info(
        s"Jobs: ${capacity.maxConcurrentJobs}\tCPU: ${capacity.maxCpuTime}\tMem: ${capacity.maxMemoryTime}\tTime: ${totalQueuedTime}\tp95: ${queuedTime95}"
      )
    }
  }

  test("find average of jobs") {
    val jobs = JobIntervalUtil.loadFromParquet("data/jobs_579.parquet")
    val result = CapacitySimulator.simulateJobSchedule[JobInterval](
      jobs = jobs,
      capacity = ClusterCapacity(1, 1, 1)
    )
    val num    = result.simulatedJobs.size.asInstanceOf[Double]
    val avgCPU = result.simulatedJobs.map(j => j.cpuTime).sum / num;
    val avgMem = result.simulatedJobs.map(j => j.memoryTime).sum / num;
    info(s"Number of jobs: ${num.asInstanceOf[Int]}\nAvg CPU: ${avgCPU}\nAvg Mem: ${avgMem}")
  }
}
