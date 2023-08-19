package rs.ac.bg.etf.jj203218m.controller

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import java.lang.Thread.sleep
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.math.pow

object RequestSender {
  def sendTestRequest(
    testerAddress: String,
    testId: Int,
    repoUrl: String,
    branchName: String,
    email: String,
    webhookAddress: String
  ): Boolean = {
    // Define the body of the request
    val data: Array[Byte] =
      s"""{
         |  "id": $testId,
         |  "repoUrl": "$repoUrl",
         |  "branchName": "$branchName",
         |  "email": "$email",
         |  "webhookAddress": "$webhookAddress"
         |}""".stripMargin.getBytes()
    // Define the request
    val request: HttpRequest = HttpRequest(
      HttpMethods.POST,
      s"$testerAddress/test-request",
      entity = HttpEntity(ContentTypes.`application/json`, data)
    )

    // Initialise variable that will contain result of this method
    var requestSent: Boolean = false
    var count: Int = 0
    // Loop until run out of retries or connect successfully
    while (count < Config.requestRetryCount && !requestSent) {
      println("Connecting to tester")

      // If retrying, sleep for some time before trying again
      if (count != 0) sleep(pow(2, count - 1).toInt * 1000)
      // Call tester API
      val result: Future[HttpResponse] =
        Http(ControllerMain.serverSystem).singleRequest(request)
      // Check the response
      val checkResult: Future[Unit] = result.map { response =>
        val statusCode = response.status.intValue()
        if (statusCode >= 200 && statusCode < 300) requestSent = true
      }
      // Wait for the requestSent variable to be populated
      Await.ready(checkResult, Duration.Inf)
      count = count + 1
    }
    requestSent
  }
}
