package com.ortaieb.wordcountsystem

import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration.{Duration, FiniteDuration}

object AppConfig {

  final case class HttpServerConfig(host: String, port: Int)
  final case class ApplicationConfig(window: Duration, compact: FiniteDuration)

  final case class Config(
      http: HttpServerConfig,
      app: ApplicationConfig
  )

  def load: Config =
    ConfigSource.default.at("word-count").loadOrThrow[Config]

}
