import java.sql.{Connection, DriverManager}


class native_db_connection {

  val url = "jdbc:mysql://localhost:3306/HSS_Reprocessing"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123"
  var connection:Connection = _

//  try {
    Class.forName(driver)
    var conn = DriverManager.getConnection(url, username, password)
    var statement = conn.createStatement
  println(statement)


  //  catch {
//}
//    case e: Exception =>   e.printStackTrace()
//
//  }
}
