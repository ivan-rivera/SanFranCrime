import org.apache.spark.sql.SparkSession
val spark = SparkSession.builder()
  .appName("SanFranCrime")
  .master("local[*]")
  .getOrCreate
import spark.implicits._

Seq((1,2), (3,4)).toDF("A", "B").show()

