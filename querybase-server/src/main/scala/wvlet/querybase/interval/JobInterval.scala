package wvlet.querybase.interval

import wvlet.airframe.codec.MessageCodec
import wvlet.log.LogSupport
import wvlet.querybase.api.interval.{IntervalLike, JobInterval}

import java.io.File
import java.sql.DriverManager
import scala.util.Using





object JobIntervalUtil extends LogSupport {

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
    Using.resource(DriverManager.getConnection("jdbc:duckdb:")) { conn =>
      Using.resource(conn.createStatement()) { stmt =>
        Using.resource(
          stmt.executeQuery(
            s"select name, created_time, start_time, finished_time, 'N/A' as sig, cpu, memory from '${parquetFilePath}'"
          )
        ) { rs =>
          while (rs.next()) {
            lst += JobInterval(
              name = rs.getString(1),
              created_time = rs.getLong(2),
              start_time = rs.getLong(3),
              finished_time = rs.getLong(4),
              sig = rs.getString(5),
              cpu = rs.getLong(6),
              memory = rs.getDouble(7)
            )
          }
        }
      }
    }
    lst.result()
  }
}
