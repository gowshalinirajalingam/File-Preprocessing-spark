import java.sql.PreparedStatement

import org.apache.spark.sql.SparkSession

class hss_file_reprocessing_implementation {
  //create a basic SparkSession
  val spark = SparkSession
    .builder().master("local")
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()


  var dbcon: Spark_db_Connection = new Spark_db_Connection()


  //CALCULATE ROW COUNT
  def CalculateRowCount(filename:String):List[(String,Int)]={


    try {
      val df = spark.read
        .format("com.databricks.spark.csv")
        .option("delimiter", "|")
        .option("header", "false")
        .load("/home/gawshalini/Documents/HSS/HSS_FILE_REPROCESSING/src/main/resources/" +filename+".dat.00.IUC")

      //df.show()


      return List((filename,df.count().toInt))
    }
    catch {
      case e: Exception =>  return List(("File not found in the location",-1))

    }
  }






  //GET line_count,processed_count for specific file name in hss_Stat_analysis_data from DB
  def getfromdb(filename:String): List[(String,Int)] ={

    //get file name from filepath

    //  var f = new File (filepath)
    //  var filenamewithExt=f.getName()

    try {

      //      val fileNameWithOutExt = FilenameUtils.removeExtension(FilenameUtils.removeExtension(FilenameUtils.removeExtension(filename)))
      //  println(fileNameWithOutExt)

//      var dbcon: db_Connection = new db_Connection()
      val hss_table = spark.read.format("jdbc").options(dbcon.options).load()
//      // hss_table.show()


      hss_table.createOrReplaceTempView(" Hss_Reprocessing")
      val hsssqlDF = spark.sql("SELECT file_name,line_count,processed_count FROM Hss_Reprocessing where file_name='" + filename + "'")
      // hsssqlDF.show()


      var linecnt: Int = hsssqlDF.select("line_count").head().get(0).asInstanceOf[Int]
      var processedcnt: Int = hsssqlDF.select("processed_count").head().get(0).asInstanceOf[Int]

      return List((filename, linecnt))
    }
    catch {
      case e: Exception =>   e.printStackTrace()
        return List(("File "+filename+" is not found in Data base", -1 ))

    }

  }



def setCurrentLineCountToDB(filename:String,currentlinecount:Int,remark:String): Unit =
  {
//
//    val conf = new SparkConf()
//    conf.setMaster("local")
//    conf.setAppName("HSS_reprocessing_update_DB")
//    conf.set("spark.driver.allowMultipleContexts", "true")
//
//    val sc = new SparkContext(conf)
//    val sqlContext = new SQLContext(sc)
//
//    val result = sqlContext.sql("CREATE TABLE IF NOT EXISTS tasks (  task_id INT AUTO_INCREMENT)")
////    hss_table.createOrReplaceTempView(" Hss_Reprocessing")
 //   val hsssqlDF = spark.sql("update hss_stat_anlysis_data set hss_stat_anlysis_data.current_line_count=" + currentlinecount + " where file_name='"+filename+"'" )
    // hsssqlDF.show()


    var  ndb:native_db_connection=new native_db_connection()
    var query:String = "update hss_stat_anlysis_data set hss_stat_anlysis_data.current_line_count=?,hss_stat_anlysis_data.remark=? where file_name=?"
    var preparedStmt:PreparedStatement = ndb.conn.prepareStatement(query);
    preparedStmt.setInt   (1, currentlinecount);
    preparedStmt.setString(3, filename);
    preparedStmt.setString(2, remark);
    println(remark)



    // execute the java preparedstatement
    preparedStmt.executeUpdate();

  //  ndb.conn.close();




  }


}
