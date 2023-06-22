package wvlet.querybase.interval

import scala.collection.mutable
import scala.util.chaining.*
import wvlet.log.LogSupport

object CapacitySimulator extends LogSupport:

  case class ClusterCapacity(
      maxConcurrentJobs: Int,
      maxCpuTime: Long,
      maxMemoryTime: Long
  )

  case class CapacitySimulatorReport[A](
      simulatedJobs: Seq[A],
      capacity: ClusterCapacity
  )

  /**
    * Simulate the job schedule with the given capacity
    * @param jobs
    * @param capacity
    *   the max cluster capacity
    * @tparam A
    * @return
    *   A list of the simulated jobs
    */
  def simulateJobSchedule[A: IntervalLike](jobs: Seq[A], capacity: ClusterCapacity): CapacitySimulatorReport[A] =
//    debug(s"Simulate job schedule with ${capacity}")

    // A queue of running jobs
    val runningQueue = new mutable.PriorityQueue[A]()(IntervalSweep.intervalSweepOrdering[A])
    // A queue of jobs which cannot be started due to the capacity limit
    val waitingQueue = new mutable.PriorityQueue[A]()(IntervalSweep.intervalStartAscendOrdering[A])

    // The simulation result will be stored in this buffer
    val simulatedJobs = Seq.newBuilder[A]

    var sweepLine = 0L

    def totalCPUTime    = runningQueue.map(_.cpuTime).sum
    def totalMemoryTime = runningQueue.map(_.memoryTime).sum

    def hasSufficientCapacityFor(job: A): Boolean =
      (runningQueue.size + 1 <= capacity.maxConcurrentJobs) &&
        (totalCPUTime + job.cpuTime <= capacity.maxCpuTime) &&
        (totalMemoryTime + job.memoryTime <= capacity.maxMemoryTime)

    var count = 0;

    /**
      * Sweep all of the jobs from the queues preceding the given sweepLimit
      * @param sweepLimit
      */
    def sweepUntil(sweepLimit: Long): Unit = {
      // Find all preceding jobs that finish before the sweepLimit
      while (runningQueue.nonEmpty && runningQueue.head.end <= sweepLimit) {
        val x = runningQueue.dequeue()
        // Report the completed job
        simulatedJobs += x
        // The running queue sort jobs by the end time, so this end value should be the smallest among the running jobs
        sweepLine = x.end

        // Find jobs in the waiting queue that can be started after the completion of the current job
        while (waitingQueue.nonEmpty && hasSufficientCapacityFor(waitingQueue.head)) {
          val j = waitingQueue.dequeue()
          // Set a new start time for the job
          val updatedJob = j.updateWith(
            created_time = j.created_time,
            start_time = sweepLine,
            finished_time = sweepLine + j.finished_time - j.start_time
          )
          // Add the job to the running queue
          runningQueue += updatedJob
        }
      }
      // Find jobs in the waiting queue that can be started after the completion of the current job
      // Update the sweep line as we processed all running jobs before the sweepLimit
      sweepLine = sweepLimit
    }

    // Sort intervals first in order to sweep the jobs from left to right
    val sortedInputJobs = jobs.sortBy(_.created_time)
    debug(s"Input list: \t${sortedInputJobs.size}")
    for (j <- sortedInputJobs) {
      sweepUntil(j.start)
      // If the queue has more slots for jobs
      // TODO Check the max cpu and memory capacity as well
      if (hasSufficientCapacityFor(j)) then
        // Assume that the job can start immediately
        val updatedJob =
          j.updateWith(
            created_time = j.created_time,
            start_time = j.created_time,
            finished_time = j.created_time + j.finished_time - j.start_time
          )
        // Add the job to the running queue
        runningQueue += updatedJob
      else
        // The job cannot be started yet, so add it to the waiting queue
        waitingQueue += j
    }
    sweepUntil(Long.MaxValue)
    // Return the simulation result
    debug(s"Waiting Queue: \t${waitingQueue.size}")
    debug(s"Schedule list: \t${simulatedJobs.result().size}")
    CapacitySimulatorReport(simulatedJobs.result(), capacity)
