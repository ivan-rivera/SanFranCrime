package DataOperations

import org.apache.spark.sql.functions.{lit, rand, when}
import org.apache.spark.sql.DataFrame
import Context.Properties

object DataLoader extends Features with ColumnOperations {

  // load data from the CSV files
  private def loadCSV(segment:String):DataFrame = {

    val contents = spark
      .read
      .format("csv")
      .option("header", "true")
      .load(Properties.inputData(segment))
      .withColumn("segment", lit(segment))

    // if loading train dataset, split it into train and validation
    val segmentedData = if(segment == "train") contents else contents.transform(createDummyResponse)

    segmentedData
      .transform(dropRedundantColumns)
      .transform(createIDColumn)
      .transform(applyAliases)
      .transform(alignColumns)

  }

  def apply():DataFrame = {
    // merge test and train data
    Properties
      .inputData
      .keys.map(s => loadCSV(s))
      .reduce(_ union _)
      .transform(generateFeatures)
      .transform(assignResponse)
      .transform(reformatColumns)
  }

}
