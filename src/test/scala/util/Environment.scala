package util

import java.util

object Environment {

  val baseURL = scala.util.Properties.envOrElse("baseURL", "http://test.api.fluentretail.com")
}
