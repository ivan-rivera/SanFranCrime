package Algorithms

import DataOperations.DataLoader
import DataOperations.ColumnOperations
import com.salesforce.op.OpWorkflow
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.features.FeatureBuilder
import com.salesforce.op.features.types.RealNN
import com.salesforce.op.stages.impl.classification.BinaryClassificationModelSelector
import com.salesforce.op.stages.impl.preparators.SanityChecker
import com.salesforce.op.stages.impl.tuning.DataCutter
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *
  * @param testMode if test mode, then the fitted models will only be evaluated on a small sample of data
  * @param testFraction proportion of data to retain for test purposes
  * @param cvFolds number of cross validation folds to use
  */
class Fit(
           val testMode:Boolean = false,
           val testFraction:Double = 0.75,
           val cvFolds:Int = 3
         ) extends Models with ColumnOperations {

  implicit val spark: SparkSession = SparkSession.builder().getOrCreate()
  import spark.implicits._

  val data = DataLoader() // load data

  // set up training data
  val trainingDataUnresolved: DataFrame = data
    .filter($"segment" === "train")
    .drop(idColumn)

  val trainingData:DataFrame = if(testMode) trainingDataUnresolved.sample(0.01) else trainingDataUnresolved

  // split predictors and response
  val (targetResponse,predictorSet) = FeatureBuilder
    .fromDataFrame[RealNN](trainingData, response = "Response")

  // feature creation
  private val featureVectorRaw = predictorSet.transmogrify()
  private val checkedFeatures = new SanityChecker()
    .setMaxCorrelation(0.95)
    .setMinVariance(0.001)
    .setRemoveBadFeatures(true)
    .setInput(targetResponse, featureVectorRaw)
    .getOutput()

  // model training parameters
  private val modelValidator = BinaryClassificationModelSelector
    .withCrossValidation(
      seed = 1,
      numFolds = 3,
      splitter = Some(DataCutter(reserveTestFraction = 0.75, seed = 1)),
      validationMetric = Evaluators.BinaryClassification.auROC,
      modelsAndParameters = modelArray
    ).setInput(targetResponse, checkedFeatures).getOutput()

  def viewModelPerformance(): Unit = {

    // train and optimise models then print model stats
    val modelWorkflow = new OpWorkflow()
      .setInputDataset(trainingData)
      .setResultFeatures(modelValidator)
      .train()

    println("Model summary:\n" + modelWorkflow.summaryPretty())

    // scoring new data can be done with:
    // modelWorkflow.setReader(testData).score().show()

  }

}
