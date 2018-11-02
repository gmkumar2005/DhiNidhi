package models

import better.files._
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorSettings
import com.mohiva.play.silhouette.impl.providers.OAuth2Settings
import play.api.{Environment, Mode}
import services.audit.SlackConfig
import util.metrics.MetricsConfig

@javax.inject.Singleton
class Configuration @javax.inject.Inject() (val cnf: play.api.Configuration, val metrics: MetricsConfig, env: Environment) {
  val debug = env.mode == Mode.Dev
  val dataDir = cnf.get[String]("data.directory").toFile
  val secretKey = cnf.get[String]("play.http.secret.key")
  val scheduledTaskEnabled = cnf.get[Boolean]("scheduled.task.enabled")

  val slackConfig = SlackConfig(
    enabled = cnf.get[Boolean]("notification.slack.enabled"),
    url = cnf.get[String]("notification.slack.url"),
    channel = cnf.get[String]("notification.slack.channel"),
    username = cnf.get[String]("notification.slack.username"),
    iconUrl = cnf.get[String]("notification.slack.iconUrl")
  )

  val authCookieSettings = {
    import scala.concurrent.duration._
    val cfg = cnf.get[play.api.Configuration]("silhouette.authenticator.cookie")

    CookieAuthenticatorSettings(
      cookieName = cfg.get[String]("name"),
      cookiePath = cfg.get[String]("path"),
      secureCookie = cfg.get[Boolean]("secure"),
      httpOnlyCookie = true,
      useFingerprinting = cfg.get[Boolean]("useFingerprinting"),
      cookieMaxAge = Some(cfg.get[Int]("maxAge").seconds),
      authenticatorIdleTimeout = Some(cfg.get[Int]("idleTimeout").seconds),
      authenticatorExpiry = cfg.get[Int]("expiry").seconds
    )
  }

  val authGoogleSettings = {
    val cfg = cnf.get[play.api.Configuration]("silhouette.authenticator.google")
    OAuth2Settings(
      authorizationURL = Some(cfg.get[String]("authorization")),
      accessTokenURL = cfg.get[String]("accessToken"),
      redirectURL = Some(cfg.get[String]("redirect")),
      apiURL = None,
      clientID = cfg.get[String]("clientId"),
      clientSecret = cfg.get[String]("clientSecret"),
      scope = Some(cfg.get[String]("scope")),
      authorizationParams = Map.empty,
      accessTokenParams = Map.empty,
      customProperties = Map.empty
    )
  }
}
