package rs.ac.bg.etf.jj203218m.tester

import java.nio.file.Paths
import java.util.UUID

import scala.language.postfixOps
import scala.sys.process._

object Tester {
  def testRepo(repoUrl: String, branchName: String): TestResult = {
    // Create directory for storing repository
    val repoDirectory = Paths.get(".", s"tmp-${UUID.randomUUID.toString}")
    val repoHandler = new GitRepoHandler(repoUrl, branchName, repoDirectory)

    repoHandler.pull()

    // Create variable for storing test logs
    var testLogs: String = ""

    // Run test
    val result: Int = {
      // Create process
      Process(Seq("cmd", "/C", Config.sbtPath, "test"), repoDirectory.toFile) !
      // Collect the process' logs
      ProcessLogger(line =>
        testLogs = testLogs + s"$line\n", s => testLogs = testLogs + s"$s\n"
      )
    }

    println(testLogs)
    println(result)

    // Delete repository
    repoHandler.deleteRepo()

    val testResult = if (result == 0) "PASSED" else "FAILED"

    // Package and return the result
    new TestResult(testResult, testLogs)
  }
}
