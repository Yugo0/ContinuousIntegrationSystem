package rs.ac.bg.etf.jj203218m.tester

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import java.lang.Thread.sleep
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.pow

object InitialisationHandler {
  def initialiseTester(address: String, level: Int): Boolean = {
    // Define the body of the request
    val data: Array[Byte] =
      s"""{
         |  "address": "$address",
         |  "level": $level,
         |  "requestRetryCount": ${Config.requestRetryCount}
         |}""".stripMargin.getBytes()
    // Define the request
    val request: HttpRequest = HttpRequest(
      HttpMethods.POST,
      s"${Config.controllerAddress}/testers",
      entity = HttpEntity(ContentTypes.`application/json`, data)
    )

    // Initialise variable that will contain result of this method
    var connected: Boolean = false
    var count: Int = 0
    // Loop until run out of retries or connect successfully
    while (count < Config.controllerRetryCount && !connected) {
      println("Connecting to controller")

      // If retrying, sleep for some time before trying again
      if (count != 0) sleep(pow(2, count - 1).toInt * 1000)
      // Call tester API
      val result: Future[HttpResponse] =
        Http(TesterMain.serverSystem).singleRequest(request)
      // Check the response
      val checkResult: Future[Unit] = result.map { response =>
        val statusCode = response.status.intValue()
        if (statusCode >= 200 && statusCode < 300) connected = true
      }
      // Wait for the requestSent variable to be populated
      Await.ready(checkResult, Duration.Inf)
      count = count + 1
    }
    connected
  }
}
