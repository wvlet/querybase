package wvlet.querybase

import wvlet.airframe.codec.MessageCodec
import wvlet.log.LogSupport

import java.io.File
import java.sql.DriverManager
import scala.util.Using

case class JobInterval(name: String, override val start: Long, override val end: Long) extends Interval(start, end) {
  override def toString: String = s"${name}:[${start}, ${end})"
}

object JobInterval extends LogSupport {

//  given IntervalLike[JobInterval]
//    extension (j: JobInterval)
//      def start: Long = j.start
//      def end: Long = j.end
//

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
              start = rs.getLong(2),
              end = rs.getLong(3)
            )
          }
        }
      }
    }
    lst.result()
  }
}
