package rs.ac.bg.etf.jj203218m.controller

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object TestService {
  // Get all tests
  def getTests: Future[Seq[Test]] = DB.connection.run(TestTable.query.result)

  // Get test by its ID
  def getTestById(id: Int): Future[Option[Test]] = {
    DB.connection.run(TestTable.query.filter(_.id === id).result.headOption)
  }

  // Insert test into database and return its ID
  def addTest(test: Test): Future[Int] = {
    DB.connection.run(TestTable.query.returning(TestTable.query.map(_.id)) += test)
  }

  // Update test's finished and succeeded values
  def updateTest(id: Int, succeeded: Boolean): Future[Int] = {
    DB.connection.run(
      TestTable.query
        .filter(_.id === id)
        .map(t => (t.finished, t.succeeded))
        .update((true, succeeded))
    )
  }
}
