package com.ortaieb.wordcountsystem.streams

import cats.effect.Timer
import com.ortaieb.wordcountsystem.AppConfig.Config
import fs2.{Pipe, Stream}

trait CompactStream[F[_]] {
  def create(config: Config, compress: Pipe[F, Unit, Unit])(
      implicit timer: Timer[F]
  ): Stream[F, Unit] =
    Stream(()).repeat
      .covary[F]
      .flatMap(Stream.emit(_).covary[F].delayBy(config.app.compact))
      .through(compress)
}

object CompactStream {
  def apply[F[_]](): CompactStream[F] = new CompactStream[F] {}
}
