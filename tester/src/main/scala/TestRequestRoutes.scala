package rs.ac.bg.etf.jj203218m.tester

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

object TestRequestRoutes {
  implicit val testRequestFormat: RootJsonFormat[TestRequest] =
    jsonFormat5(TestRequest)

  private val route: String = "test-request"

  // Receive test request
  val testRequest: Route = path(route) {
    post {
      // Get request body
      entity(as[TestRequest]) { testRequest =>
        // Validate request body
        validateTestRequest(testRequest)
        // Process test request in a separate thread
        Future {
          // Run tests
          val testResult: TestResult = Tester.testRepo(
            testRequest.repoUrl,
            testRequest.branchName
          )
          // Send test result via email
          EmailSender.sendTestResultsEmail(
            testRequest.email,
            testRequest.repoUrl,
            testResult
          )
          // Report test results to the webhook
          RequestSender.sendTestResultsToWebhook(
            testRequest.webhookAddress,
            testResult
          )
          // Report to controller that the testing is complete
          RequestSender.sendTestResultsToController(testResult, testRequest.id)
        }
        // Respond to the request
        complete(StatusCodes.OK)
      }
    }
  }

  private val emailRegex: Regex = """^(\w+(.\w+)+)@(\w+(.\w+)+)$""".r
  private val urlRegex: Regex =
    """^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]""".r

  private def validateTestRequest(testRequest: TestRequest): Unit = {
    if (!emailRegex.matches(testRequest.email))
      throw new Exception("Email is not valid")
    if (!urlRegex.matches(testRequest.repoUrl))
      throw new Exception("Repository URL is not valid")
    if (
      testRequest.webhookAddress != ""
      && !urlRegex.matches(testRequest.webhookAddress)
    )
      throw new Exception("Webhook address is not valid")
  }
}
