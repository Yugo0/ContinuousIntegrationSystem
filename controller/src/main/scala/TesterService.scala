package rs.ac.bg.etf.jj203218m.controller

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object TesterService {
  // Get all testers
  def getTesters: Future[Seq[Tester]] = DB.connection.run(TesterTable.query.result)

  // Get tester by its ID
  def getTesterById(id: Int): Future[Option[Tester]] = {
    DB.connection.run(TesterTable.query.filter(_.id === id).result.headOption)
  }

  // Get tester by its address
  def getTesterByAddress(address: String): Future[Option[Tester]] = {
    DB.connection.run(
      TesterTable.query.filter(_.address === address).result.headOption
    )
  }

  // Get tester by ID of a test assigned to it
  def getTesterByTestId(id: Int): Future[Option[Tester]] = {
    val query = for {
      // Map query into a tuple
      (tester, _) <- TesterTable.query
        .join(TestTable.query)
        // Join tables on id of the first table (tester)
        // and tester_id of the second (test)
        .on(_.id === _.testerId)
        // Compare the id of the second table (test)
        .filter(_._2.id === id)
    } yield tester // Retrieve tester value form the tuple
    DB.connection.run(query.result.headOption)
  }

  // Get available tester by its ID
  def getAvailableTesterById(id: Int): Future[Option[Tester]] = {
    DB.connection.run(
      TesterTable.query
        .filter(
          t =>
            t.id === id // Check tester's id
            && t.available === true // Check if tester is available
            && !t.id.in( // Check if tester is processing any tests
              // Grab all tester IDs form the tests that are still being processed
              TestTable.query.filter(_.finished === false).map(_.testerId)
            )
        ).result.headOption
    )
  }

  // Get most viable tester for provided level and requestRetryCount
  def getAvailableTesterByLevelAndRequestRetryCount(
    level: Int,
    requestRetryCount: Int
  ): Future[Option[Tester]] = {
    DB.connection.run(
      TesterTable.query
        .filter(
          t =>
            t.level <= level // Check tester's level
            && t.available === true // Check if tester is available
            && !t.id.in( // Check if tester is processing any tests
              // Grab all tester IDs form the tests that are still being processed
              TestTable.query.filter(_.finished === false).map(_.testerId)
            )
        ).sortBy(t => {
          val requestRetryCountSort = {
            Case
              // Retry count matches the passed one
              .If(t.requestRetryCount === requestRetryCount)
              .Then(0)
              // Retry count is greater that the passed one
              .If(t.requestRetryCount > requestRetryCount)
              .Then(t.requestRetryCount - requestRetryCount)
              // Retry count is lower than the passed one
              .Else((t.requestRetryCount - 2 * Constants.maxRetryCount).abs)
          }
          // Sort by level first, then retry count
          (t.level.desc, requestRetryCountSort)
        }).result.headOption
    )
  }

  // Insert tester into database
  def addTester(tester: Tester): Future[Int] = {
    DB.connection.run(TesterTable.query += tester)
  }

  // Update tester
  def updateTester(id: Int, tester: Tester): Future[Int] = {
    DB.connection.run(
      TesterTable.query.filter(_.id === id)
        .map(t => (t.address, t.level, t.requestRetryCount, t.available))
        .update(
          (tester.address, tester.level, tester.requestRetryCount, tester.available)
        )
    )
  }

  // Update tester's available value
  def updateTesterAvailable(id: Int, available: Boolean): Future[Int] = {
    DB.connection.run(
      TesterTable.query.filter(_.id === id).map(t => t.available).update(available)
    )
  }
}
