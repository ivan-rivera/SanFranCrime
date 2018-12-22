// Project properties

package Context

import com.typesafe.config.{Config, ConfigFactory}

// the actual settings can be found under src/main/resources/application.conf
object Properties {

  private val conf: Config = ConfigFactory.load("application.conf") // read the app config

  // APPLICATION SETTINGS
  val appName: String = conf.getString("appName")
  val masterURL: String = conf.getString("masterURL")

  // INPUT SOURCES
  val inputData = Map(
    "train" -> conf.getString("trainDataPath"),
    "test" -> conf.getString("testDataPath")
  )

}
