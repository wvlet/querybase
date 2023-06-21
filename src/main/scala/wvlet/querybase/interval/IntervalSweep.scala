package wvlet.querybase.interval

import wvlet.querybase.interval.IntervalLike

object IntervalSweep:

  /**
    * An ordering definition to sweep intervals with smaller end first
    *
    * @tparam A
    * @return
    */
  def intervalSweepOrdering[A: IntervalLike]: Ordering[A] = new Ordering[A]:
    override def compare(x: A, y: A): Int =
      val diff = y.end - x.end
      if diff == 0 then y.start.compareTo(x.start)
      else if diff < 0 then -1
      else 1

  case class IntervalSweepReport[A](
      sweepLine: Long,
      overlappedIntervals: Seq[A]
  ) {
    def count: Int = overlappedIntervals.size
  }

  /**
    * Sweep the sorted intervals and report the overlapped at each sweep line
    *
    * @param sortedIntervals
    * @param reporter
    * @tparam A
    */
  def sweep[A: IntervalLike](sortedIntervals: Iterator[A], reporter: IntervalSweepReport[A] => Unit): Unit = {
    import scala.collection.mutable

    // Sort intervals in the queue by the end of the interval
    val overlappedIntervals = new mutable.PriorityQueue[A]()(intervalSweepOrdering[A])
    var sweepLine           = Long.MinValue

    val intervalsAtTheSweepLine = mutable.TreeMap.empty[Long, Seq[A]]

    def sweepUntil(sweepLimit: Long): Long = {
      // Remove all intervals that end before the sweep limit
      while (overlappedIntervals.nonEmpty && overlappedIntervals.head.end <= sweepLimit) {
        val x = overlappedIntervals.dequeue()
        intervalsAtTheSweepLine += x.end -> overlappedIntervals.toSeq
      }
      // Report peak intervals until the sweep line
      val precedingIntervals: Seq[(Long, Seq[A])] = intervalsAtTheSweepLine.range(Long.MinValue, sweepLimit).toSeq
      precedingIntervals
        .sortBy(_._1)
        .foreach { (pos, lst) =>
          reporter(IntervalSweepReport(pos, lst))
          intervalsAtTheSweepLine.remove(pos)
          sweepLimit
        }
      sweepLimit
    }

    while (sortedIntervals.hasNext) {
      val x = sortedIntervals.next()
      sweepLine = sweepUntil(x.start)
      overlappedIntervals += x
      intervalsAtTheSweepLine += sweepLine -> overlappedIntervals.toSeq
    }
    sweepLine = sweepUntil(Long.MaxValue)
  }
