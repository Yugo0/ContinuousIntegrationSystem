package rs.ac.bg.etf.jj203218m.tester

import com.typesafe.config.{Config, ConfigFactory}

import scala.math.{min, max}

object Config {
  // Load configuration file
  private val config: Config = ConfigFactory.load("application.conf")

  // Load values from configuration file
  val sbtPath: String = config.getString("sbt.path")
  val smtpServer: String = config.getString("mail.smtp.server")
  val smtpPort: Int = config.getInt("mail.smtp.port")
  val mailAccount: String = config.getString("mail.account")
  val mailPassword: String = config.getString("mail.password")
  val requestRetryCount: Int = min(
    max(config.getInt("request.retryCount"), Constants.minRetryCount),
    Constants.maxRetryCount
  )
  val controllerRetryCount: Int = min(
    max(config.getInt("controller.retryCount"), Constants.minRetryCount),
    Constants.maxRetryCount
  )
  val controllerAddress: String = config.getString("controller.address")
}
