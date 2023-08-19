package rs.ac.bg.etf.jj203218m.listener

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


object TestResultRoutes {
   implicit val testRequestFormat: RootJsonFormat[TestResultRequest] =
     jsonFormat1(TestResultRequest)

   private val route: String = "test-result"

   val testResult: Route = path(route) {
     post {
       // Get request body
       entity(as[TestResultRequest]) {testResultRequest =>
         println(testResultRequest.data)
         // Respond to the request
         complete(StatusCodes.OK)
       }
     }
   }
}
