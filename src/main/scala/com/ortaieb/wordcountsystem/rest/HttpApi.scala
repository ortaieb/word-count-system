package com.ortaieb.wordcountsystem.rest

import cats.effect.{Clock, IO}
import com.ortaieb.wordcountsystem.{EventType, Store}
import com.ortaieb.wordcountsystem.model.CountedRecord
import org.http4s.implicits._
import org.http4s.{HttpApp, HttpRoutes, server}

/**
  */
class HttpApi(store: Store[IO, EventType, CountedRecord])(implicit clock: Clock[IO]) {
  def routes: HttpRoutes[IO] = CountController(store).routes

  val httpApp: HttpApp[IO] = server.Router("word" -> routes).orNotFound
}

object HttpApi {
  def apply(store: Store[IO, EventType, CountedRecord])(implicit clock: Clock[IO]): HttpApi =
    new HttpApi(store)
}
