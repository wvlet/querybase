package wvlet.querybase.interval

import scala.collection.mutable
import scala.util.chaining.*

object CapacitySimulator:

  case class ClusterCapacity(
      maxConcurrentJobs: Int,
      maxCpuTime: Long,
      maxMemoryTime: Long
  )

  /**
    * Simulate the job schedule with the given capacity
    * @param jobs
    * @param capacity
    * @tparam A
    * @return
    */
  def simulateJobSchedule[A: IntervalLike](jobs: Seq[A], capacity: ClusterCapacity): Seq[A] =
    // A queue of running jobs
    val runningQueue = new mutable.PriorityQueue[A]()(IntervalSweep.intervalSweepOrdering[A])
    // A queue of jobs which cannot be started due to the capacity limit
    val waitingQueue = new mutable.PriorityQueue[A]()(IntervalSweep.intervalStartAscendOrdering[A])

    // The simulation result will be stored in this buffer
    val simulatedJobs = Seq.newBuilder[A]

    var sweepLine = 0L

    def sweepUntil(sweepLimit: Long): Unit = {
      while (runningQueue.nonEmpty && runningQueue.head.end <= sweepLimit) {
        val x = runningQueue.dequeue()
        simulatedJobs += x
        sweepLine = x.end

        while (waitingQueue.nonEmpty && (runningQueue.size < capacity.maxConcurrentJobs)) {
          val j = waitingQueue.dequeue()
          val updatedJob = j.updateWith(
            created_time = j.created_time,
            start_time = sweepLine,
            finished_time = sweepLine + j.finished_time - j.start_time
          )
          runningQueue += updatedJob
        }
      }
      sweepLine = sweepLimit
    }

    // Sort intervals first in order to sweep the jobs from left to right
    val sortedInputJobs = jobs.sortBy(_.created_time)
    for (j <- sortedInputJobs) {
      sweepUntil(j.start)
      // If the queue has more slots for jobs
      // TODO Check the max cpu and memory capacity as well
      if (runningQueue.size < capacity.maxConcurrentJobs) then
        // Assume that the job can start immediately
        val updatedJob =
          j.updateWith(
            created_time = j.created_time,
            start_time = j.start_time,
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
    simulatedJobs.result()
