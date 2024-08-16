package com.chophan

import org.apache.spark.sql.{
  SparkSession,
  SaveMode
}
import org.apache.spark.sql.functions.{
  lit,
  max
}
import org.apache.hadoop.fs.{
  FileSystem,
  Path
}

object Ingestion {
  def main(args: Array[String]): Unit = {
    val tableName = args(0)
    val tableId = args(1)
    val executionDate = args(2)
    
    val runTime = executionDate.split("-")
    val year = runTime(0)
    val month = runTime(1)
    val day = runTime(2)
    
    val spark =
      SparkSession
        .builder()
        .getOrCreate()
    
    val tableLocation = s"hdfs://172.18.0.2:9000/user/spark/lake/$tableName"
    val conf = spark.sparkContext.hadoopConfiguration
    val path = new Path(s"$tableLocation")
    val fs = FileSystem.get(path.toUri, conf)
    val exists = fs.exists(path)
    var tableQuery = ""
    if (exists) {
      val maxId =
        spark
          .read
          .format("parquet")
          .load(tableLocation)
          .agg(max(s"$tableId"))
          .head()
          .getInt(0)
      
      tableQuery = s"(SELECT * FROM $tableName WHERE $tableId > $maxId) AS data"
    } else {
      tableQuery = s"(SELECT * FROM $tableName) AS data"
    }
    
    val data =
      spark
        .read
        .format("jdbc")
        .options(
          Map(
            "url" -> "jdbc:mysql://172.18.0.5:3306/chophan",
            "driver" -> "com.mysql.jdbc.Driver",
            "dbtable" -> tableQuery,
            "user" -> "spark",
            "password" -> "chophan"
          )
        )
        .load()
    
    data
      .withColumn("year", lit(year))
      .withColumn("month", lit(month))
      .withColumn("day", lit(day))
      .write
      .format("parquet")
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day")
      .save(tableLocation)
  }
}
