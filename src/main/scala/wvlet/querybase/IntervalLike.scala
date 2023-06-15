package wvlet.querybase

trait IntervalLike[A]:
  extension (a: A)
    def start: Long
    def end: Long
    def length: Long                                   = end - start
    def contains[B: IntervalLike](other: B): Boolean   = a.start <= other.start && other.end <= a.end
    def contains(pos: Long): Boolean                   = a.start <= pos && pos < a.end
    def precedes[B: IntervalLike](other: B): Boolean   = a.end <= other.start
    def follows[B: IntervalLike](other: B): Boolean    = a.start >= other.end
    def intersects[B: IntervalLike](other: B): Boolean = a.start < other.end && other.start < a.end
    def overlaps(start: Long, end: Long): Boolean      = a.start < end && start <= a.end
