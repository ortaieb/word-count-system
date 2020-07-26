package com.ortaieb.wordcountsystem.streams

import cats.effect.{Blocker, ContextShift, Sync}
import fs2.Stream
import fs2.io.stdinUtf8

trait StdInSource[F[_]] {
  def source(implicit sync: Sync[F], contextShift: ContextShift[F]): Stream[F, String] =
    for {
      blocker <- Stream.resource(Blocker[F])
      stdin <- stdinUtf8[F](1024, blocker)
    } yield stdin
}

object StdInSource {
  def apply[F[_]](): StdInSource[F] = new StdInSource[F] {}
}
