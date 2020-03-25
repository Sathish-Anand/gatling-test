package authentication

import io.gatling.core.ConfigKeys.data
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class authdetails extends Simulation {

   val auth = scenario("authentication").
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
//       session =>
//       println(session("auth_token").as[String])
//       session
       session =>session.set("auth_token","${auth_token}").set("client_ids","${client_id}")
         println(session("auth_token").as[String])
         println(session("client_id").as[String])
         session
     }

//  val session: Session = session.set("auth_token","${auth_token}")

  val sessionHeaders = Map("Authorization" -> "Bearer ${auth_token}",
     "Content-Type" -> "application/json")
}
