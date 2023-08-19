package rs.ac.bg.etf.jj203218m.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.util._

object TesterRoutes {
  implicit val testerFormat: RootJsonFormat[Tester] = jsonFormat5(Tester)
  implicit val testerRequestFormat: RootJsonFormat[TesterRequest] =
    jsonFormat3(TesterRequest)

  private val route: String = "testers"

  // Get all testers
  val getTesters: Route = path(route) {
    get {
      onComplete(TesterService.getTesters) {
        // Request to database successful
        case Success(value) => complete(value)
        // Error while processing request
        case Failure(_) => complete(StatusCodes.InternalServerError)
      }
    }
  }

  // Get a tester by its ID
  val getTester: Route = path(route / IntNumber) { id =>
    get {
      onComplete(TesterService.getTesterById(id)) {
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

  // Add a new tester
  val addTester: Route = path(route) {
    post {
      // Get request body
      entity(as[TesterRequest]) {testerRequest =>
        // Look for an existing tester by its address
        onComplete(TesterService.getTesterByAddress(testerRequest.address)) {
          // Error while processing request
          case Failure(_) => complete(StatusCodes.InternalServerError)
          // Request to database successful
          case Success(value) => value match {
            // Something was found in database
            case Some(existingTester) =>
              // Create a Tester object to update existing tester in database
              val tester = Tester(
                existingTester.id,
                testerRequest.address,
                testerRequest.level,
                testerRequest.requestRetryCount,
                available = true
              )
              onComplete(TesterService.updateTester(existingTester.id, tester)) {
                // Request to database successful
                case Success(_) => complete(StatusCodes.OK)
                // Error while processing request
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }

            // Nothing was found in database
            case None =>
              // Create new tester object to put in the database
              val tester = Tester(
                0,
                testerRequest.address,
                testerRequest.level,
                testerRequest.requestRetryCount,
                available = true
              )
              onComplete(TesterService.addTester(tester)) {
                // Request to database successful
                case Success(_) => complete(StatusCodes.OK)
                // Error while processing request
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
          }
        }
      }
    }
  }
}
