package scala.today

import sttp.model.Uri

case class Config(
  port: Int,
  jdbcUrl: String,
  dbUser: String,
  dbPassword: String,
  baseScaladexUrl: Uri,
  runScrapingJobs: Boolean
)
