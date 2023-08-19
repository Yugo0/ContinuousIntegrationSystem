package rs.ac.bg.etf.jj203218m.controller

import com.typesafe.config.{Config, ConfigFactory}

import scala.math.{min, max}

object Config {
  // Load configuration file
  private val config: Config =
    ConfigFactory.load("application.conf")

  // Load values from configuration file
  val requestRetryCount: Int = min(
    max(config.getInt("request.retryCount"), Constants.minRetryCount),
    Constants.maxRetryCount
  )
}
