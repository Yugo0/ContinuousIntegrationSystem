package rs.ac.bg.etf.jj203218m.controller

import slick.jdbc.JdbcBackend.Database

object DB {
  // Load database configuration
  val connection = Database.forConfig("postgres")
}
