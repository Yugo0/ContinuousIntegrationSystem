package rs.ac.bg.etf.jj203218m.controller

case class Test(
  id: Int,
  repoUrl: String,
  branchName: String,
  email: String,
  webhookAddress: String,
  finished: Boolean,
  succeeded: Boolean,
  tester_id: Int
)
