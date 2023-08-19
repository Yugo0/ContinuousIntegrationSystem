package rs.ac.bg.etf.jj203218m.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

object ControllerMain {
  // Define ActorSystem to be used by the server
  implicit val serverSystem: ActorSystem = ActorSystem("server-system")

  def main(args: Array[String]): Unit = {
    // Extract parameters
    val interface: String = args(0)
    val port: Int = args(1).toInt

    // Add routes
    val routes: Route =
      TesterRoutes.getTesters ~
      TesterRoutes.getTester ~
      TesterRoutes.addTester ~
      TestRoutes.addTest ~
      TestRoutes.updateTest ~
      TestRoutes.getTest ~
      TestRoutes.getTests ~
      TestRoutes.getTesterByTestId

    // Start the server
    Http().newServerAt(interface, port).bind(routes)

    println(s"Server started at http://$interface:$port")
  }
}
