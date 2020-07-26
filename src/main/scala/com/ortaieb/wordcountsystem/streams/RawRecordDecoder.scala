package com.ortaieb.wordcountsystem.streams

import cats.effect.IO
import com.ortaieb.wordcountsystem.model.{CountedRecord, RawRecord}
import fs2.Pipe
import io.circe.parser.decode

trait RawRecordDecoder[F[_], V] {
  val decodeStep: Pipe[F, String, V]
}

object RawRecordDecoder {

  val countedRecordIoDecoder: RawRecordDecoder[IO, CountedRecord] =
    new RawRecordDecoder[IO, CountedRecord] {
      override val decodeStep: Pipe[IO, String, CountedRecord] = stream =>
        stream
          .map(line => decode[RawRecord](line).toOption)
          .filter(_.isDefined)
          .map(raw => CountedRecord.apply(raw.get))
    }
}
