//package order
//
//import io.gatling.core.Predef._
//import io.gatling.core.scenario.Simulation
//import io.gatling.http.Predef._
//import util.{Environment, Headers}
//
//import scala.concurrent.duration._
//import scala.util.Random
//
//class orderTest extends Simulation {
//
//  var randomString = Iterator.continually(Map("randstring" -> ( Random.alphanumeric.take(5).mkString )))
//
//  val httpConfiguration = http.baseUrl(Environment.baseURL).
//    headers(Headers.commonHeader)
//
//  val scn = scenario("Create ORDER").
//    feed(randomString).
//    exec(http("CreateHDOrder").
//      post("/order").
//      headers(Headers.commonHeader).
//      body(StringBody(""" { "retailerId":"1", "type":"HD", "orderRef":"HD-${randstring}", "customer":{ "firstName":"SATHISH", "lastName":"TEST", "customerRef":"Sathsih", "mobile":"0432964439", "email":"sathish.anand@fluentcommerce.com" }, "items":[ { "skuRef":"SKU-1", "skuPrice":19.99, "totalPrice":59.99, "requestedQty":1 } ], "fulfilmentChoice":{ "fulfilmentType":"HD_PFS", "fulfilmentPrice":0, "fulfilmentTaxPrice":0, "deliveryType":"STANDARD", "deliveryInstruction":"home delivery", "address":{ "name":"TESTORDER", "street":"46 Kippax Street", "city":"Surry Hills", "postcode":"2010", "state":"NSW", "country":"AU" } } }""")).
//      check(status.is(200)).
//      check(jsonPath("$.id").saveAs("order_id"))).
//      pause(5)
//
//  .exec(http("getOrder").
//      get("/order/${order_id}").
//      headers(Headers.commonHeader).
//      check(status.is(200)).
//      check(bodyString.saveAs("ORDER_BODY"))
//    )
//    .exec{
//      session =>
//        println(session("ORDER_BODY").as[String])
//        session
//    }
//      .pause(5)
//
//  setUp(
//    scn.inject(rampConcurrentUsers(0) to (6) during(10),
//      constantConcurrentUsers(6) during(60 seconds))).protocols(httpConfiguration)
////    scn.inject(constantUsersPerSec(2)
////      during (1) randomized)).protocols(httpConfiguration)
//
//    .assertions(
//      global.responseTime.max.lt(1000),
//      forAll.responseTime.max.lt(1000),
//      details("getOrder").responseTime.max.lt(1000),
//
//      global.successfulRequests.percent.is(100)
//
//    )
//}
//
