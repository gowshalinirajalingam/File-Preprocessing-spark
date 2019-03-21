
name := "HSS_FILE_REPROCESSING"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies += "com.rabbitmq" % "amqp-client" % "2.4.1"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = true)
assemblyJarName in assembly := "hss_file_reprocessing.jar"

assemblyMergeStrategy in assembly := {
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", "services", "org.apache.hadoop.fs.FileSystem") => MergeStrategy.filterDistinctLines
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat

  case PathList("com","rabbitmq", xs @ _*) => MergeStrategy.last

  case x => MergeStrategy.last
}


