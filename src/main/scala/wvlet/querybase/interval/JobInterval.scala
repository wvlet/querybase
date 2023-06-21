package wvlet.querybase.interval

import wvlet.airframe.codec.MessageCodec
import wvlet.log.LogSupport
import wvlet.querybase.interval.{IntervalLike, JobInterval}

import java.io.File
import java.sql.DriverManager
import scala.util.Using

case class JobInterval(
    name: String,
    created_time: Long,
    start_time: Long,
    finished_time: Long,
    sig: String,
    cpu: Long,
    memory: Long
) {}

object JobInterval extends LogSupport {

  given IntervalLike[JobInterval] with
    extension (j: JobInterval)
      def start: Long         = j.created_time
      def end: Long           = j.finished_time
      def created_time: Long  = j.created_time
      def start_time: Long    = j.start_time
      def finished_time: Long = j.finished_time
      def updateWith(created_time: Long, start_time: Long, finished_time: Long): JobInterval =
        j.copy(created_time = created_time, start_time = start_time, finished_time = finished_time)

  def loadFromJson(jsonFilePath: String): Seq[JobInterval] = {
    // read the json file and map to JobInterval by using airframe-codec
    val jsonData = wvlet.airframe.control.IO.readAsString(new File(jsonFilePath))
    debug(jsonData)
    val jobList = MessageCodec.of[Seq[JobInterval]].fromJson(jsonData)
    jobList
  }

  def loadFromParquet(parquetFilePath: String): Seq[JobInterval] = {
    Class.forName("org.duckdb.DuckDBDriver")
    val lst = Seq.newBuilder[JobInterval]
    Using(DriverManager.getConnection("jdbc:duckdb:")) { conn =>
      Using(conn.createStatement()) { stmt =>
        Using(stmt.executeQuery(s"select * from '${parquetFilePath}'")) { rs =>
          while (rs.next()) {
            lst += JobInterval(
              name = rs.getString(1),
              created_time = rs.getLong(2),
              start_time = rs.getLong(3),
              finished_time = rs.getLong(4),
              sig = rs.getString(5),
              cpu = rs.getLong(6),
              memory = rs.getLong(7)
            )
          }
        }
      }
    }
    lst.result()
  }
}
