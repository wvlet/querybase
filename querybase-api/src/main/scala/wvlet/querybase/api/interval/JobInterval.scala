package wvlet.querybase.api.interval

import wvlet.log.LogSupport

case class JobInterval(
    name: String,
    created_time: Long,
    start_time: Long,
    finished_time: Long,
    sig: String,
    cpu: Long,
    memory: Double
) {}

object JobInterval extends LogSupport {

  /**
    * A type class adaptoer for making JobInterval looks like IntervalLike class
    */
  given IntervalLike[JobInterval] with
    extension (j: JobInterval)
      def start: Long         = j.created_time
      def end: Long           = j.finished_time
      def created_time: Long  = j.created_time
      def start_time: Long    = j.start_time
      def finished_time: Long = j.finished_time
      def cpuTime: Long       = j.cpu
      def memoryTime: Double  = j.memory
      def updateWith(created_time: Long, start_time: Long, finished_time: Long): JobInterval =
        j.copy(created_time = created_time, start_time = start_time, finished_time = finished_time)
}
