scalaVersion := "2.13.12"
version := "1.0"

libraryDependencies ++=
  Seq(
    "org.apache.spark" %% "spark-core" % "3.3.0",
    "org.apache.spark" %% "spark-sql" % "3.3.0"
  )

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "etl",
      organization := "com.chophan"
    )
