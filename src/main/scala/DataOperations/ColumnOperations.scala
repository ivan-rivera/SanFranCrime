// some tools to manage the input data

package DataOperations

import DataOperations.DataLoader.spark
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, lit, monotonically_increasing_id, when}

trait ColumnOperations {

  import spark.implicits._

  // recommended order:
  // drop redundant variables (train)
  // create ID variable (train)
  // apply aliases (train and test)
  // align variables
  // join

  val redundantColumns: Array[String] = Array("Descript", "Resolution")
  val responseColumn: String =  "Category"
  val idColumn: String = "Id"

  def applyAliases(df: DataFrame): DataFrame = {
    df
      .withColumnRenamed("Dates","DateTime")
      .withColumnRenamed("pdDistrict", "District")
      .withColumnRenamed("X", "Longitude")
      .withColumnRenamed("Y", "Latitude")
  }

  def dropRedundantColumns(df: DataFrame): DataFrame = {
    df.drop(redundantColumns.toSeq:_*)
  }

  def createDummyResponse(df: DataFrame): DataFrame = {
    df.withColumn("Category", lit("unknown"))
  }

  // for simplicity, lets try predicting theft-related crimes alone
  // Transmogrifai isnt great for dealing with multi-class problems,
  // so if we had to do this for all classes, we would take a
  // one-vs-rest approach
  def assignResponse(df:DataFrame): DataFrame = {
    df.withColumn("Response",
      when($"Category".rlike("THEFT|STOLEN"), lit(1))
        .otherwise(lit(0)))
      .withColumn("Response", $"Response".cast("double"))
      .drop("Category")
  }

  def createIDColumn(df: DataFrame): DataFrame = {
    df.withColumn(idColumn, lit(-1) * monotonically_increasing_id())
    // multiply by -1 to avoid conflicts with the second dataset
  }

  def reformatColumns(df:DataFrame): DataFrame = {
    df
      .withColumn("Latitude", $"Latitude".cast("double"))
      .withColumn("Longitude", $"Longitude".cast("double"))
  }

  // before stacking datasets we should make sure the order of variables is the same
  def alignColumns(df: DataFrame): DataFrame = {
    val variableArray = Array(
      idColumn,
      "Category",
      "Segment",
      "DateTime",
      "DayOfWeek",
      "District",
      "Address",
      "Latitude",
      "Longitude"
    )
    df.select(variableArray.toSeq.map(col):_*)
  }

}
