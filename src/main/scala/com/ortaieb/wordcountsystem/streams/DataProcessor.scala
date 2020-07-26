package com.ortaieb.wordcountsystem.streams

import com.ortaieb.wordcountsystem.Store
import com.ortaieb.wordcountsystem.model.Conv
import fs2.{Pipe, Stream, text}
import io.circe.Decoder
import io.circe.parser.decode

trait DataProcessor[F[_], K, V, V2] {

  def process(
      source: Stream[F, String],
      store: Store[F, K, V]
  )(implicit conv: Conv[V2, V], decoder: Decoder[V2]): Stream[F, Int] =
    source
      .through(text.lines)
      .through(decodeLine)
      .through(append(store))

  def append(store: Store[F, K, V]): Pipe[F, V, Int] =
    stream => stream.evalMap(store.append)

  def decodeLine(implicit conv: Conv[V2, V], decoder: Decoder[V2]): Pipe[F, String, V] =
    _.map(line => decode[V2](line).toOption)
      .filter(_.isDefined)
      .map(opt => conv.conv(opt.get))

}

object DataProcessor {
  def apply[F[_], K, V, V2]: DataProcessor[F, K, V, V2] = new DataProcessor[F, K, V, V2] {}
}
