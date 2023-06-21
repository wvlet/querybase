package wvlet.querybase.interval

trait IntervalLike[A]:
  extension (a: A)
    def start: Long
    def created_time: Long
    def start_time: Long
    def finished_time: Long
    def updateWith(created_time: Long, start_time: Long, finished_time: Long): A
    def end: Long
    def cpuTime: Long
    def memoryTime: Double
    def length: Long                                   = end - start
    def contains[B: IntervalLike](other: B): Boolean   = a.start <= other.start && other.end <= a.end
    def contains(pos: Long): Boolean                   = a.start <= pos && pos < a.end
    def precedes[B: IntervalLike](other: B): Boolean   = a.end <= other.start
    def follows[B: IntervalLike](other: B): Boolean    = a.start >= other.end
    def intersects[B: IntervalLike](other: B): Boolean = a.start < other.end && other.start < a.end
    def overlaps(start: Long, end: Long): Boolean      = a.start < end && start <= a.end
