package simulationExecutor

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import util.{Environment, Headers}

import scala.util.Random

class order extends Simulation
{
  var randomString = Iterator.continually(Map("randstring" -> (Random.alphanumeric.take(5).mkString)))

  val authfeeder = jsonFile("data/test.json")

  val httpConfiguration = http.baseUrl(Environment.baseURL)

  val authenticator = scenario("OAuth").
    feed(authfeeder).
    exec(authentication.authdetails.auth)

  val headers_2 = Map(
    "Content-Type" -> "application/json",
    //    "fluent.account" -> "${client_id}",
    "Authorization" -> "bearer ${auth_token}"
  )

  val hdorder = scenario("HDOrder").
    feed(randomString).
    exec(order.HDOrder.hdOrder)


  setUp(
    authenticator.inject(atOnceUsers(1)),
    hdorder.inject(atOnceUsers(1)))
    //    scn.inject(rampConcurrentUsers(0) to (6) during(10),
    //      constantConcurrentUsers(6) during(60 seconds)))
    .protocols(httpConfiguration)


    .assertions(
      global.responseTime.max.lt(1000),
      forAll.responseTime.max.lt(1000),
      details("getOrder").responseTime.max.lt(1000),

      global.successfulRequests.percent.is(100)

    )
}