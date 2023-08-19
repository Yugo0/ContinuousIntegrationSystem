package rs.ac.bg.etf.jj203218m.tester

import courier._
import Defaults._

import javax.mail.internet.InternetAddress

import scala.language.postfixOps
import scala.util._

object EmailSender {
  private val mailer: Mailer = Mailer(Config.smtpServer, Config.smtpPort)
    .auth(true)
    .as(Config.mailAccount, Config.mailPassword)
    .startTls(true)()

  def sendTestResultsEmail(
    to: String,
    repoUrl: String,
    testResult: TestResult
  ): Unit = {
    mailer(
      Envelope
      .from(new InternetAddress(Config.mailAccount))
      .to(new InternetAddress(to))
      .subject(s"Test result - $repoUrl - ${testResult.getResult}")
      .content(Multipart().html(testResultHtmlTemplate(repoUrl, testResult)))
    ).onComplete {
      case Success(_) => println(s"Message delivered to $to")
      case Failure(ex) => println(s"Message failed with error: $ex")
    }
  }

  private def testResultHtmlTemplate(
    repoUrl: String,
    testResult: TestResult
  ): String = {
    val logs = testResult.getLogs.replace("\n", "<br>")
    s"""
       |<!DOCTYPE html>
       |<html lang="en">
       |<head>
       |  <meta charset="UTF-8">
       |  <meta name="viewport" content="width=device-width,initial-scale=1">
       |  <meta name="x-apple-disable-message-reformatting">
       |  <title></title>
       |</head>
       |<body style="margin:0;padding:0;">
       |  <h1>
       |    Test result - $repoUrl
       |  </h1>
       |  <p>
       |    Test result - ${testResult.getResult}
       |  </p>
       |  <p>
       |    Logs:
       |  </p>
       |  <p>
       |    <code>
       |      $logs
       |    </code>
       |  </p>
       |</body>
       |""".stripMargin
  }
}
