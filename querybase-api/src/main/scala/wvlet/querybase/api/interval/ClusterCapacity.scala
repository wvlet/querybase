package wvlet.querybase.api.interval

case class ClusterCapacity(
    maxConcurrentJobs: Int,
    maxCpuTime: Long,
    // TODO Change this type to Double, but it causes java.lang.NoSuchMethodError: 'long wvlet.querybase.api.interval.ClusterCapacity.maxMemoryTime()'
    //	at wvlet.querybase.server.QuerybaseServer$.router$$anonfun$29(QuerybaseServer.scala:9)
    //	at wvlet.airframe.surface.StaticMethodParameter.get$$anonfun$1(Surfaces.scala:109)
    //	at scala.Option.map(Option.scala:242)
    //	at wvlet.airframe.surface.StaticMethodParameter.get(Surfaces.scala:109)
    maxMemoryTime: Double
)

case class CapacitySimulatorReport[A](
    simulatedJobs: Seq[A],
    capacity: ClusterCapacity
)
