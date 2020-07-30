package com.ortaieb.wordcountsystem

import cats.effect.{ExitCode, IO, IOApp, Resource, SyncIO}

import com.ortaieb.wordcountsystem.model.CountedRecord
import com.ortaieb.wordcountsystem.rest.HttpApi
import com.ortaieb.wordcountsystem.streams.{CompactStream, DataProcessor, StdInSource}
import fs2._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

/**
  */
object Main extends IOApp.WithContext {

  implicit val ec = ExecutionContext.global

  def startAllServices(): Stream[IO, Unit] = {
    val config = AppConfig.load
    val store = new InMemoryStore(config.app.window)

    import com.ortaieb.wordcountsystem.model.CountedRecord.countedRecordDecoder

    val messageProc =
      DataProcessor[IO, EventType, CountedRecord].process(StdInSource[IO].source, store)(
        countedRecordDecoder
      )

    val compress: Pipe[IO, Unit, Unit] = stream => stream.evalMap(_ => store.compressData)
    val compactProc = CompactStream[IO].create(config, compress)

    val httpApi = new HttpApi(store)

    val webService = BlazeServerBuilder[IO](ec)
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(httpApi.httpApp)
      .serve

    Stream(messageProc, compactProc, webService).parJoin(3).map(_ => ())
  }

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource.liftF(SyncIO(ec))

  override def run(args: List[String]): IO[ExitCode] =
    startAllServices().compile.drain.as(ExitCode.Success)
}
