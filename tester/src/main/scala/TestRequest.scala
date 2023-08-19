package rs.ac.bg.etf.jj203218m.tester

case class TestRequest(
  id: Int,
  repoUrl: String,
  branchName: String,
  email: String,
  webhookAddress: String
)
