package rs.ac.bg.etf.jj203218m.tester

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import java.lang.Thread.sleep
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.pow
import scala.util._

object RequestSender {
  def sendTestResultsToWebhook(
    webhookAddress: String,
    testResult: TestResult
  ): Unit = {
    if (webhookAddress == "") return
    // Define the body of the request
    val data: Array[Byte] = s"""{"data": "${testResult.getResult}"}""".getBytes()
    // Define the request
    val request: HttpRequest = HttpRequest(
      HttpMethods.POST,
      webhookAddress,
      entity = HttpEntity(ContentTypes.`application/json`, data)
    )
    sendRequest(request)
  }

  def sendTestResultsToController(testResult: TestResult, testId: Int): Unit = {
    val succeeded: Boolean = if (testResult.getResult == "PASSED") true else false
    // Define the body of the request
    val data: Array[Byte] = s"""{"succeeded": $succeeded}""".getBytes()
    // Define the request
    val request: HttpRequest = HttpRequest(
      HttpMethods.PATCH,
      s"${Config.controllerAddress}/tests/$testId",
      entity = HttpEntity(ContentTypes.`application/json`, data)
    )
    sendRequest(request)
  }

  private def sendRequest(request: HttpRequest): Unit = {
    def sendRequestInternal(count: Int): Unit = {
      // If no more retries are left, return
      if (count >= Config.requestRetryCount) return
      // If retrying, sleep for some time before trying again
      if (count != 0) sleep(pow(2, count - 1).toInt * 1000)
      println(s"Sending request to ${request.uri}")

      // Send request
      val result = Http(TesterMain.serverSystem).singleRequest(request)
      result.onComplete {
        case Success(response) =>
          // Connected to the target successfully
          val statusCode = response.status.intValue()
          if (statusCode >= 200 && statusCode < 300) {
            // Request successful
            println(s"Request to ${request.uri} sent successfully")
          } else {
            // Bad status code, resend request
            println(s"Request to ${request.uri} unsuccessful")
            sendRequestInternal(count + 1)
          }
        case Failure(_) =>
          // Couldn't connect to the target, resend request
          println(s"Request to ${request.uri} unsuccessful")
          sendRequestInternal(count + 1)
      }
    }
    // Call internal function
    sendRequestInternal(0)
  }
}
