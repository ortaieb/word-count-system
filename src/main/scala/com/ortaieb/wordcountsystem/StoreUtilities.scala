package com.ortaieb.wordcountsystem

import com.ortaieb.wordcountsystem.model.CountedRecord

import scala.concurrent.duration.Duration

object StoreUtilities {
  def filterStale(now: Long, seq: Seq[CountedRecord])(window: Duration): Seq[CountedRecord] =
    seq.filter(e => now - e.timestamp <= window.toSeconds)

  def group(seq: Seq[CountedRecord]): Seq[CountedRecord] =
    seq
      .groupMapReduce(_.unique)(_.cnt)(_ + _)
      .map {
        case ((et, d, ts), v) => CountedRecord(et, d, ts, v)
      }
      .toSeq

  def wordsCount(seq: Seq[CountedRecord]): Map[Data, Int] =
    seq.groupMapReduce(_.data)(_.cnt)(_ + _)
}
