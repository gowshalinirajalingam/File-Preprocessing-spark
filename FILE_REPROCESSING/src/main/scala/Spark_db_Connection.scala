



class Spark_db_Connection {

  val host: String = "localhost"
  val port: Int = 3306
  val database: String = "HSS_Reprocessing"
  val table: String = "hss_stat_anlysis_data"
  val user: String = "root"
  val password: String = "123"

  val options = Map(
    "url" -> s"jdbc:mysql://$host:$port/$database?zeroDateTimeBehavior=convertToNull",
    "dbtable" -> table,
    "user" -> user,
    "password" -> password,
    "driver"->"com.mysql.jdbc.Driver"
  )



}
