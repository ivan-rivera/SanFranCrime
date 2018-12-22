package DataOperations

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{year, month, weekofyear, dayofmonth, hour, when, lit}

trait Features {

  implicit val spark: SparkSession = SparkSession.builder().getOrCreate()
  import spark.implicits._

  // create extra features
  def generateFeatures(df:DataFrame): DataFrame = {
    df
      .withColumn("year", year($"DateTime")) // extract year
      .withColumn("month", month($"DateTime"))
      .withColumn("week", weekofyear($"DateTime"))
      .withColumn("day_of_month", dayofmonth($"DateTime"))
      .withColumn("hour", hour($"DateTime"))
      .withColumn("intersection", when($"Address".contains("/"), lit(1)).otherwise(lit(0)))
      .drop("DateTime","Address")

  }

}
