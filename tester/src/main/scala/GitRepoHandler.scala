package rs.ac.bg.etf.jj203218m.tester

import org.eclipse.jgit.api.{CreateBranchCommand, Git}

import java.nio.file.Path
import scala.reflect.io.Directory

class GitRepoHandler(repoUrl: String, branchName: String, repoDirectory: Path) {
  def pull(): Unit = {
    val repo = Git
      .cloneRepository()
      .setURI(repoUrl)
      .setDirectory(repoDirectory.toFile)
      .call()

    if (branchName != "")
      repo
        .checkout()
        .setCreateBranch(true)
        .setName(branchName)
        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
        .setStartPoint("origin/" + branchName)
        .call()
  }
  def deleteRepo(): Unit = {
    val dir = new Directory(repoDirectory.toFile)
    dir.deleteRecursively()
  }
}
