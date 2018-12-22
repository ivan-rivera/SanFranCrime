package Algorithms

import com.salesforce.op.stages.impl.classification.{OpLogisticRegression, OpRandomForestClassifier}
import org.apache.spark.ml.tuning.ParamGridBuilder

// model contents and definitions
trait Models {

  // LOGISTIC REGRESSION
  private val logReg = new OpLogisticRegression()
  private val logRegParams = new ParamGridBuilder()
    .addGrid(logReg.fitIntercept)
    .addGrid(logReg.regParam, Array(0.01, 0.2)) // regularization values
    .addGrid(logReg.elasticNetParam, Array(0.0, 0.1)) // elastic net values
    .addGrid(logReg.maxIter, Array(100))
    .build()

  // RANDOM FOREST (it's reasonably fast, although boosting might work better here)
  private val rfModel = new OpRandomForestClassifier()
  private val rfParams = new ParamGridBuilder()
    .addGrid(rfModel.subsamplingRate, Array(0.75,1.0)) // sub-sampling rate
    .addGrid(rfModel.numTrees, Array(50, 150)) // trees to grow
    .addGrid(rfModel.maxDepth, Array(5, 10)) // maximum depth
    .build()

  // public output
  val modelArray = Seq(
    logReg -> logRegParams,
    rfModel -> rfParams
  )

}
