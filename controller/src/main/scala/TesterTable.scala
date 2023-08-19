package rs.ac.bg.etf.jj203218m.controller

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, TableQuery}

class TesterTable(tag: Tag) extends Table[Tester](tag, "tester") {
  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val address: Rep[String] = column[String]("address")
  val level: Rep[Int] = column[Int]("level")
  val requestRetryCount: Rep[Int] = column[Int]("request_retry_count")
  val available: Rep[Boolean] = column[Boolean]("available")
  def * : ProvenShape[Tester] =
    (id, address, level, requestRetryCount, available).mapTo[Tester]
}

object TesterTable {
  val query = TableQuery[TesterTable]
}