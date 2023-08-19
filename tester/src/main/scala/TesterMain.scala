package rs.ac.bg.etf.jj203218m.tester

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

object TesterMain {
  // Define ActorSystem to be used by the server
  implicit val serverSystem: ActorSystem = ActorSystem("server-system")

  def main(args: Array[String]): Unit = {
    // Extract parameters
    val interface: String = args(0)
    val port: Int = args(1).toInt
    val address: String = args(2)
    val level: Int = args(3).toInt

    // Connect tester to the controller
    val initialised: Boolean =
      InitialisationHandler.initialiseTester(address, level)

    if (!initialised) {
      // If connection failed, shut down
      println("Failed to connect to controller")
      sys.exit(1)
    }

    // Start the server
    Http().newServerAt(interface, port).bind(TestRequestRoutes.testRequest)

    println(s"Server started at http://$interface:$port")
  }
}
