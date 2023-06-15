package wvlet.querybase

/**
  * Represent a time range [start, end)
  */
class Interval(val start: Long, val end: Long):
  require(start <= end, "start must be smaller than or equals to end: [%d, %d)".format(start, end))
  override def toString = s"[${start},${end})"

  /**
    * Check if this interval contains the other interval
    * @param other
    * @return
    */
  def contains(other: Interval): Boolean =
    start <= other.start && other.end <= end

  def preceeds(other: Interval): Boolean =
    end <= other.start

  def overlaps(other: Interval): Boolean =
    start < other.end && other.start < end

// case class JobInterval(name: String, start: Long, end: Long) extends Interval(st)
// case class TaskInterval(taskName: String, start: Long, end: Long) extends Interval
