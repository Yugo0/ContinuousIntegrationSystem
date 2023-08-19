package rs.ac.bg.etf.jj203218m.controller

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}

class TestTable(tag: Tag) extends Table[Test](tag, "test") {
  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val repoUrl: Rep[String] = column[String]("repo_url")
  val branchName: Rep[String] = column[String]("branch_name")
  val email: Rep[String] = column[String]("email")
  val webhookAddress: Rep[String] = column[String]("webhook_address")
  val finished: Rep[Boolean] = column[Boolean]("finished")
  val succeeded: Rep[Boolean] = column[Boolean]("succeeded")
  val testerId: Rep[Int] = column[Int]("tester_id")
  def * : ProvenShape[Test] =
    (id, repoUrl, branchName, email, webhookAddress, finished, succeeded, testerId)
      .mapTo[Test]
}

object TestTable {
  val query = TableQuery[TestTable]
}