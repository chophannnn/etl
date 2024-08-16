package com.chophan

import org.apache.spark.sql.{
  SparkSession,
  SaveMode
}
import org.apache.spark.sql.functions.{
  sum,
  desc,
  lit
}

object Transformation {
  def main(args: Array[String]): Unit = {
    val executionDate = args(0)
    
    val runTime = executionDate.split("-")
    val year = runTime(0)
    val month = runTime(1)
    val day = runTime(2)
    
    val spark =
      SparkSession
        .builder()
        .config("hive.metastore.uris", "thrift://172.18.0.3:9083")
        .config("hive.exec.dynamic.partition", "true")
        .config("hive.exec.dynamic.partition.mode", "nonstrict")
        .enableHiveSupport()
        .getOrCreate()
    
    val genres = spark.read.format("parquet").load("hdfs://172.18.0.2:9000/user/spark/lake/genres").drop("year", "month", "day")
    val brands = spark.read.format("parquet").load("hdfs://172.18.0.2:9000/user/spark/lake/brands").drop("year", "month", "day")
    val products = spark.read.format("parquet").load("hdfs://172.18.0.2:9000/user/spark/lake/products").drop("year", "month", "day")
    
    val reports =
      products
        .join(genres, products("genre_id") === genres("genre_id"), "inner")
        .join(brands, products("brand_id") === brands("brand_id"), "inner")
        .groupBy(
          brands("brand_name")
        )
        .agg(
          sum(products("rating")).as("total_rating")
        )
        .orderBy(desc("total_rating"))
        .limit(10)
    
    reports
      .withColumn("year", lit(year))
      .withColumn("month", lit(month))
      .withColumn("day", lit(day))
      .write
      .format("hive")
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day")
      .saveAsTable("reports.number_of_rating_by_brands")
  }
}
