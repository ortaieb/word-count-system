package com.ortaieb.wordcountsystem.rest

import cats.effect.{Clock, IO}
import com.ortaieb.wordcountsystem.model.{CountedRecord, WcResponse}
import com.ortaieb.wordcountsystem.{EventType, Store, StoreUtilities}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

case class CountController(store: Store[IO, EventType, CountedRecord])(implicit clock: Clock[IO])
    extends Http4sDsl[IO] {

  import com.ortaieb.wordcountsystem.eventTypeSeqEntityEncoder
  import com.ortaieb.wordcountsystem.model.WcResponse.wcResponseEntityEncoder

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "count" / eventType =>
      for {
        raw <- store.rawResults(eventType)
        fresh <- IO(StoreUtilities.wordsCount(raw._2))
        s <- Ok(WcResponse(eventType, raw._1, fresh))
      } yield s

    case GET -> Root / "count" =>
      for {
        types <- store.eventTypes
        s <- Ok(types)
      } yield s

  }

}
