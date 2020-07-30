package com.ortaieb.wordcountsystem.streams

import com.ortaieb.wordcountsystem.Store
import fs2.{Pipe, Stream, text}
import io.circe.Decoder
import io.circe.parser.decode

trait DataProcessor[F[_], K, V] {

  def process(
      source: Stream[F, String],
      store: Store[F, K, V]
  )(implicit decoder: Decoder[V]): Stream[F, Int] =
    source
      .through(text.lines)
      .through(decodeLine)
      .through(append(store))

  def decodeLine(implicit decoder: Decoder[V]): Pipe[F, String, V] =
    _.map(line => decode[V](line).toOption)
      .filter(_.isDefined)
      .map(_.get)

  def append(store: Store[F, K, V]): Pipe[F, V, Int] =
    stream => stream.evalMap(store.append)

}

object DataProcessor {
  def apply[F[_], K, V]: DataProcessor[F, K, V] = new DataProcessor[F, K, V] {}
}
