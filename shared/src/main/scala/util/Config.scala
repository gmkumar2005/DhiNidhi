package util

object Config {
  final val projectId = Version.projectId
  final val projectName = Version.projectName
  final val version = Version.version
  final val metricsId = projectId.replaceAllLiterally("-", "_")
  final val projectUrl = "https://github.com/gmkumar2005/DhiNidhi"
  final val adminEmail = "admin@dhiNIdhi.com"
  final val pageSize = 100
}
