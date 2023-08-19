package rs.ac.bg.etf.jj203218m.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.util._
import scala.util.matching.Regex

object TestRoutes {
  implicit val testFormat: RootJsonFormat[Test] = jsonFormat8(Test)
  implicit val testerFormat: RootJsonFormat[Tester] = jsonFormat5(Tester)
  implicit val updateTestRequestFormat: RootJsonFormat[UpdateTestRequest] =
    jsonFormat1(UpdateTestRequest)
  implicit val testRequestFormat: RootJsonFormat[TestRequest] =
    jsonFormat7(TestRequest)

  private val route: String = "tests"

  // Get all tests
  val getTests: Route = path(route) {
    get {
      onComplete(TestService.getTests) {
        // Request to database successful
        case Success(value) => complete(value)
        // Error while processing request
        case Failure(_) => complete(StatusCodes.InternalServerError)
      }
    }
  }

  // Get a test by its ID
  val getTest: Route = path(route / IntNumber) { id =>
    get {
      onComplete(TestService.getTestById(id)) {
        // Error while processing request
        case Failure(_) => complete(StatusCodes.InternalServerError)
        // Request to database successful
        case Success(value) => value match {
          // Something was found in database
          case Some(value) => complete(value)
          // Nothing was found in database
          case None => complete(StatusCodes.NotFound)
        }
      }
    }
  }

  // Get a tester by its test's ID
  val getTesterByTestId: Route = path(route / IntNumber / "tester") { id =>
    get {
      onComplete(TesterService.getTesterByTestId(id)) {
        // Error while processing request
        case Failure(_) => complete(StatusCodes.InternalServerError)
        // Request to database successful
        case Success(value) => value match {
          // Something was found in database
          case Some(value) => complete(value)
          // Nothing was found in database
          case None => complete(StatusCodes.NotFound)
        }
      }
    }
  }

  // Update a test
  val updateTest: Route = path(route / IntNumber) { id =>
    patch {
      // Get request body
      entity(as[UpdateTestRequest]) { updateTestRequest =>
        onComplete(TestService.updateTest(id, updateTestRequest.succeeded)) {
          // Request to database successful
          case Success(_) => complete(StatusCodes.OK)
          // Error while processing request
          case Failure(_) => complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  // Add a new test
  val addTest: Route = path(route) {
    post {
      // Get request body
      entity(as[TestRequest]) {testRequest =>
        // Validate request body
        validateTestRequest(testRequest)
        // Based on received data, get the correct data from the database
        onComplete(
          if (testRequest.testerId != 0) {
            // Tester's ID is defined, use it get a tester
            TesterService.getAvailableTesterById(testRequest.testerId)
          } else
            // Tester's ID is not defined, use other parameters to pick a tester
            TesterService.getAvailableTesterByLevelAndRequestRetryCount(
              testRequest.level,
              testRequest.requestRetryCount
            )
        ) {
          // Error while processing request
          case Failure(_) => complete(StatusCodes.InternalServerError)
          // Request to database successful
          case Success(value) => value match {
            // Nothing was found in database
            case None => complete(StatusCodes.NotFound)
            // Something was found in database
            case Some(tester) =>
              // Create new Test object to put in the database
              val newTest = Test(
                0,
                testRequest.repoUrl,
                testRequest.branchName,
                testRequest.email,
                testRequest.webhookAddress,
                finished = false,
                succeeded = false,
                tester.id
              )
              onComplete(TestService.addTest(newTest)) {
                // Error while processing request
                case Failure(_) => complete(StatusCodes.InternalServerError)
                // Request to database successful
                case Success(testId) =>
                  // Send testing request to chosen tester
                  val requestSent: Boolean = RequestSender.sendTestRequest(
                    tester.address,
                    testId,
                    testRequest.repoUrl,
                    testRequest.branchName,
                    testRequest.email,
                    testRequest.webhookAddress
                  )
                  if (requestSent)
                    // Testing request sent successfully
                    complete(StatusCodes.OK)
                  else {
                    // Testing request failed to send
                    // Set test as unsuccessfully finished
                    TestService.updateTest(testId, succeeded = false)
                    // Set tester as unavailable
                    TesterService.updateTesterAvailable(
                      tester.id,
                      available = false
                    )
                    complete(StatusCodes.ServiceUnavailable)
                  }
              }
          }
        }
      }
    }
  }

  // Regular expression for validation of emails
  private val emailRegex: Regex = """^(\w+(.\w+)+)@(\w+(.\w+)+)$""".r
  // Regular expression for validation of URLs
  private val urlRegex: Regex =
    """^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]""".r

  // TestRequest validation
  private def validateTestRequest(testRequest: TestRequest): Unit = {
    // Validate email
    if (!emailRegex.matches(testRequest.email))
      throw new Exception("Email is not valid")
    // Validate repoUrl
    if (!urlRegex.matches(testRequest.repoUrl))
      throw new Exception("Repository URL is not valid")
    // Validate webhookAddress
    if (
      testRequest.webhookAddress != ""
      && !urlRegex.matches(testRequest.webhookAddress)
    )
      throw new Exception("Webhook address is not valid")
  }
}
