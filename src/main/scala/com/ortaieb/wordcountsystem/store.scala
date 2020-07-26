package com.ortaieb.wordcountsystem

import cats.effect.{Clock, IO}
import com.ortaieb.wordcountsystem.model.CountedRecord

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

trait Store[F[_], K, V] {
  def append(record: V): F[Int]
  def compressData(implicit clock: Clock[F]): F[Unit]
  def compressData(eventType: K)(implicit clock: Clock[F]): F[Seq[V]]
  def eventTypes: F[Seq[K]]
  def rawResults(key: K)(implicit clock: Clock[F]): F[(Timestamp, Seq[V])]
}

class InMemoryStore(window: Duration, init: Map[EventType, Seq[CountedRecord]] = Map.empty)
    extends Store[IO, EventType, CountedRecord] {

  private val content: TrieMap[EventType, Seq[CountedRecord]] = TrieMap.from(init)

  override def append(record: CountedRecord): IO[Int] = IO {
    content.update(
      record.eventType,
      record +: content.getOrElse(record.eventType, Seq.empty[CountedRecord])
    )

    content.get(record.eventType).size
  }

  override def compressData(implicit clock: Clock[IO]): IO[Unit] = IO {
    content.keySet.foreach(compressData(_))
  }

  override def compressData(
      eventType: EventType
  )(implicit clock: Clock[IO]): IO[Seq[CountedRecord]] =
    for {
      refTs <- now
      curr <- data(eventType)
      fresh <- filterStale(refTs, curr)
      grpd <- group(fresh)
      _ <- update(eventType, grpd)
    } yield grpd

  override def eventTypes: IO[Seq[EventType]] = IO(content.keySet.toSeq)

  override def rawResults(
      key: EventType
  )(implicit clock: Clock[IO]): IO[(Timestamp, Seq[CountedRecord])] =
    for {
      refTs <- now
      raw <- data(key)
      fresh <- filterStale(refTs, raw)
    } yield (refTs, fresh)

  private def now(implicit clock: Clock[IO]): IO[Timestamp] =
    clock.realTime(SECONDS)

  private def data(eventType: EventType): IO[Seq[CountedRecord]] =
    IO(content.getOrElse(eventType, Seq.empty[CountedRecord]))

  private def filterStale(now: Long, seq: Seq[CountedRecord]): IO[Seq[CountedRecord]] =
    IO(StoreUtilities.filterStale(now, seq)(window))

  private def group(seq: Seq[CountedRecord]): IO[Seq[CountedRecord]] =
    IO(StoreUtilities.group(seq))

  private def update(eventType: EventType, records: Seq[CountedRecord]): IO[Unit] =
    IO(content.update(eventType, records))

}
