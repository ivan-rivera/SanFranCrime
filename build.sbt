// -------------------------------------------------------------------------
// SBT BUILD DEFINITION
// -------------------------------------------------------------------------

// -------------------------------------------------------------------------
// general
// -------------------------------------------------------------------------
name := "SanFranCrime"

// -------------------------------------------------------------------------
// versions
// -------------------------------------------------------------------------
version := "0.1"
scalaVersion := "2.11.8"

val sparkVersion = "2.3.2"
val scalaTestVersion = "3.0.5"
val typeSafeConfig = "1.3.2"
val transmogrifaiVersion = "0.5.0"

// -------------------------------------------------------------------------
// dependencies
// -------------------------------------------------------------------------

resolvers ++= Seq(
  "AkkaRepository" at "http://repo.akka.io/releases/",
  "apache-snapshots" at "http://repository.apache.org/snapshots/", // Spark
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/", // some useful utils
  Resolver.jcenterRepo // for transmogrif.ai
)


// Libraries used
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "com.typesafe" % "config" % typeSafeConfig,
  "com.salesforce.transmogrifai" %% "transmogrifai-core" % transmogrifaiVersion
)

// -------------------------------------------------------------------------
// extra arguments
// -------------------------------------------------------------------------

fork in run := true
trapExit := false

initialCommands in console := """
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._
  val spark = SparkSession.builder()
    .master("local")
    .appName("SanFranCrime")
    .getOrCreate()
  import spark.implicits._
  val sc = spark.sparkContext
"""

cleanupCommands in console := "spark.stop()"
