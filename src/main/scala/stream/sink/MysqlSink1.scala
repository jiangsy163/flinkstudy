package stream.sink

import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}

import org.apache.flink.api.common.io.OutputFormat
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.slf4j.{Logger, LoggerFactory}
import stream.state.SensorReading

/**
  * @author lj
  * @createDate 2019/12/27 17:36
  **/

class MysqlSink1 extends OutputFormat[SensorReading] {

  val logger: Logger = LoggerFactory.getLogger("MysqlSink")
  var conn: Connection = _
  var ps: PreparedStatement = _
  val jdbcUrl = "jdbc:mysql://192.168.229.128:3306?useSSL=false&allowPublicKeyRetrieval=true"
  val username = "root"
  val password = "123456"
  val driverName = "com.mysql.jdbc.Driver"

  override def open(taskNumber: Int, numTasks: Int): Unit = {

    Class.forName(driverName)
    try {
      Class.forName(driverName)
      conn = DriverManager.getConnection(jdbcUrl, username, password)

      // close auto commit
      conn.setAutoCommit(false)
    } catch {
      case e@(_: ClassNotFoundException | _: SQLException) =>
        logger.error("init mysql error")
        e.printStackTrace()
        System.exit(-1);
    }
  }

  /**
    * 吞吐量不够话，可以将数据暂存在状态中，批量提交的方式提高吞吐量（如果oom，可能就是数据量太大，资源没有及时释放导致的）
    * @param user
    */
  override def writeRecord(user: SensorReading): Unit = {
    println("get user : " + user.toString)
//    ps = conn.prepareStatement("insert into async.user(username, password, sex, phone) values(?,?,?,?)")
//    ps.setString(1, user.id)
//    ps.setString(2, user.id)
//    ps.setInt(3, user.temperature)
//    ps.setString(4, user.phone)

    ps.execute()
    conn.commit()
  }
  override def close(): Unit = {
    if (conn != null){
      conn.commit()
      conn.close()
    }
  }

  override def configure(parameters: Configuration): Unit = {

  }
}
