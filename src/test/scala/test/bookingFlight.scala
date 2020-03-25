package test
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random

class bookingFlight extends Simulation {

	val httpProtocol = http
		.baseUrl("https://cheeze-flight-booker.herokuapp.com")
		.inferHtmlResources()
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
		.silentResources

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Accept" -> "*/*",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9")

	val headers_2 = Map(
		"Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Pragma" -> "no-cache")

	val headers_11 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Origin" -> "https://cheeze-flight-booker.herokuapp.com",
		"Upgrade-Insecure-Requests" -> "1")



	val csvFeeder = csv("data/PassengersInfo.csv")

	val randomEmailFeeder = Iterator.continually(Map("randomEmail" -> (Random.alphanumeric.take(20).mkString + "@ggmail.com")))

	//val csvFeeder = csv("data/PassengersInfo.csv").random
	//val customSeparatorFile = separatedValues("myFeederFile.txt",'#')

	//val csvFeeder1 = csv("data/PassengersInfo.csv").batch
	//val csvFeeder2 = csv("data/PassengersInfo.csv").batch(200)


	val reqHomepage = exec(http("Homepage")                  // ----
		.get("/")																														//     |
		.headers(headers_0)																									//     |
		.resources(http("request_1")														//     |
			.get("/assets/application-2534172286055efef05dbb34d2da8fc2.js")		//	   | -- stores the 'Homepag' request in a variable
			.headers(headers_1))																							//     |
		.check(status.in(200, 201, 202, 304))											//	   |
		.check(status.not(404)))																	// ----

	val scn = scenario("SE")

		.exec(flushHttpCache)
		.exec(flushSessionCookies)
		.exec(flushCookieJar)

		.repeat(2)           // Repeats the loop a specified amount of times - here 2 times
		{
			reqHomepage								 // variable containing request 'Homepage'
		}

		.during(60)         // Iterates over the loop for the specified amount of time - here 60 seconds
		{
			reqHomepage
		}


		.forever{                    // Iterates over the loop forever
			pause(1)
		}

		.pause(1)
		.exec(http("request_2")
			.get("/favicon.ico")
			.headers(headers_2)
			.silent)
		.pause(10)

		.exec(http("SearchFlight")
			.get("/flights?utf8=%E2%9C%93&from=1&to=2&date=2015-01-03&num_passengers=2&commit=search")
			.headers(headers_0)
			.resources(http("request_4")
				.get("/assets/application-2534172286055efef05dbb34d2da8fc2.js")
				.headers(headers_1),
				http("request_5")
					.get("/assets/application-c99cbb3caf78d16bb1482ca2e41d7a9c.css"))
			.check(currentLocationRegex(".*num_passengers=2.*")))
		.pause(1)
		.exec(http("request_6")
			.get("/favicon.ico")
			.silent)
		.pause(10)

		.exec(http("SelectFlight")
			.get("/bookings/new?utf8=%E2%9C%93&flight_id=10&commit=Select+Flight&num_passengers=2")
			.headers(headers_0)
			.resources(http("request_8")
				.get("/assets/application-2534172286055efef05dbb34d2da8fc2.js")
				.headers(headers_1),
				http("request_9")
					.get("/assets/application-c99cbb3caf78d16bb1482ca2e41d7a9c.css"))
			.check(css("h1:contains('Book Flight')").exists)
			.check(substring("Email").find.exists)
			.check(substring("Email"))
			.check(substring("Email").count.is(2))
			.check(css("input[name='authenticity_token']", "value").saveAs("authToken"))
			.check(bodyString.saveAs("BODY")))
		.exec{
			session =>
				println(session("BODY").as[String])
				session
		}
		.pause(1)
		.exec(http("request_10")
			.get("/favicon.ico")
			.silent)
		.pause(10)

		//.feed(csvFeeder)
		.feed(csvFeeder, 2)
		.feed(randomEmailFeeder, 2)

		.exec(http("BookFlight")
			.post("/bookings")
			.headers(headers_11)
			.formParam("utf8", "✓")
			.formParam("authenticity_token", "${authToken}")
			.formParam("booking[flight_id]", "10")
			.formParam("booking[passengers_attributes][0][name]", "${name1}")
			.formParam("booking[passengers_attributes][0][email]", "${randomEmail1}")
			.formParam("booking[passengers_attributes][1][name]", "${name2}")
			.formParam("booking[passengers_attributes][1][email]", "${randomEmail2}")
			.formParam("commit", "Book Flight"))
		.pause(1)
		.exec(http("request_12")
			.get("/favicon.ico")
			.silent)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
		.assertions(
			global.responseTime.max.lt(1000),
			forAll.responseTime.max.lt(1000),
			details("BookFlight").responseTime.max.lt(1000),
			global.successfulRequests.percent.is(100)
		)
}