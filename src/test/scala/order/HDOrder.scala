package order

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

object HDOrder extends simulationExecutor.order {

  val hdOrder = scenario("Create ORDER").
    exec(http("CreateHDOrder").
      post("/order").
      body(StringBody(""" { "retailerId":"1", "type":"HD", "orderRef":"HD-${randstring}", "customer":{ "firstName":"SATHISH", "lastName":"TEST", "customerRef":"Sathsih", "mobile":"0432964439", "email":"sathish.anand@fluentcommerce.com" }, "items":[ { "skuRef":"SKU-1", "skuPrice":19.99, "totalPrice":59.99, "requestedQty":1 } ], "fulfilmentChoice":{ "fulfilmentType":"HD_PFS", "fulfilmentPrice":0, "fulfilmentTaxPrice":0, "deliveryType":"STANDARD", "deliveryInstruction":"home delivery", "address":{ "name":"TESTORDER", "street":"46 Kippax Street", "city":"Surry Hills", "postcode":"2010", "state":"NSW", "country":"AU" } } }""")).
      headers(headers_2).
      check(status.is(200)).
      check(jsonPath("$.id").saveAs("order_id")).
      check(bodyString.saveAs("ORDER_BODY"))).
    pause(5)

    .exec(http("getOrder").
      get("/order/${order_id}").
      headers(headers_2).
      check(status.is(200)).
      check(bodyString.saveAs("ORDER_BODY"))
    )

}

