/*
*
* Crime Classifier
*
* */

// imports
import org.apache.spark.sql.SparkSession
import Context.Properties
import Algorithms.Fit

object Main extends App {

  // initialize the spark session
  implicit val spark: SparkSession = SparkSession
    .builder()
    .appName(Properties.appName)
    .master(Properties.masterURL)
    .getOrCreate()

  // in order to tweak candidate models, edit Algorithm.Models.scala
  val ModelFitter = new Fit(testMode = false, testFraction = 0.75, cvFolds = 3)
  ModelFitter.viewModelPerformance() // check performance stats

  spark.stop() // terminate the session
}
