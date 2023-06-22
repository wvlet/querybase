package wvlet.querybase.api.interval

case class ClusterCapacity(
    maxConcurrentJobs: Int,
    maxCpuTime: Long,
    maxMemoryTime: Double
)

case class CapacitySimulatorReport[A](
    simulatedJobs: Seq[A],
    capacity: ClusterCapacity
)
