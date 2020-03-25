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

  val auth = scenario("authentication").
    feed(authfeeder).
    exec(http("OAuthentication").
      post("/oauth/token").
      queryParam("username", "${username}").
      queryParam("password", "${password}").
      queryParam("client_id", "${client_id}").
      queryParam("client_secret", "${client_secret}").
      queryParam("scope", "api").
      queryParam("grant_type", "password").
      check(status.is(200)).
      check(jsonPath("$.access_token").saveAs("auth_token"))).
    pause(5)
    .exec{
             session =>
             println(session("auth_token").as[String]) ; session
//      session =>session.set("auth_token","${auth_token}").set("client_ids","${client_id}")
//        println(session("auth_token").as[String])
//        println(session("client_id").as[String])
//        session
    }

  //  val session: Session = session.set("auth_token","${auth_token}")

//  val session: Session = session.set("auth_token","${auth_token}")
  val headers_2 = Map(
    "Content-Type" -> "application/json",
    //    "fluent.account" -> "${client_id}",
    "Authorization" -> "bearer ${auth_token}"
  )

  val hdorder = scenario("HDOrder").
    feed(randomString).
    exec(order.HDOrder.hdOrder)

  setUp(
    auth.inject(atOnceUsers(1)),
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