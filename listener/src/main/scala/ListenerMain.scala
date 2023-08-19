package rs.ac.bg.etf.jj203218m.listener

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

object ListenerMain {
  // Define ActorSystem to be used by the server
  implicit val serverSystem: ActorSystem = ActorSystem("server-system")

  def main(args: Array[String]): Unit = {
    // Extract parameters
    val interface: String = args(0)
    val port: Int = args(1).toInt

    // Start the server
    Http().newServerAt(interface, port).bind(TestResultRoutes.testResult)

    println(s"Server started at http://$interface:$port")
  }
}
