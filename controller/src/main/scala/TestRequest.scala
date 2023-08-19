package rs.ac.bg.etf.jj203218m.controller

case class TestRequest(
  repoUrl: String,
  branchName: String,
  email: String,
  webhookAddress: String,
  testerId: Int,
  level: Int,
  requestRetryCount: Int
)
