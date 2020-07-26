package com.ortaieb.wordcountsystem.streams

import cats.effect.{Clock, IO}
import com.ortaieb.wordcountsystem.model.Conv
import com.ortaieb.wordcountsystem.{Store, Timestamp}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import fs2._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

/**
  */
class DataProcessorSpec extends AnyWordSpec with Matchers with DataProcessorFixture {

  "DataProcessor" must {
    "process stream data to Store of single record" in {
      val processor = DataProcessor[IO, Int, Int, SingleNumber]
      val input = Stream("""{ "n" : 1 }""")[IO]
      val store = TestStore(2.minutes)
      processor.process(input, store).compile.drain.unsafeRunSync()
      store.content(1) mustBe List(1)
    }

    "populate the instances list in store" in {
      val processor = DataProcessor[IO, Int, Int, SingleNumber]
      val input = Stream(s"""{ "n" : 1 }${System.lineSeparator}{ "n" : 1 }""")
      val store = TestStore(2.minutes)

      processor.process(input, store).compile.drain.unsafeRunSync()
      store.content(1) mustBe List(1, 1)
    }

    "skip invalid lines of input" in {
      val processor = DataProcessor[IO, Int, Int, SingleNumber]
      val input =
        Stream(s"""{ "n" : 1 }${System.lineSeparator}{xxx${System.lineSeparator}{ "n" : 1 }""")
      val store = TestStore(2.minutes)

      processor.process(input, store).compile.drain.unsafeRunSync()
      store.content(1) mustBe List(1, 1)
    }
  }
}

trait DataProcessorFixture {

  case class TestStore(window: Duration, content: TrieMap[Int, Seq[Int]] = TrieMap.empty)
      extends Store[IO, Int, Int] {
    override def append(record: Int): IO[Int] = IO {
      content.update(record, record +: content.getOrElse(record, Seq.empty))
      content.getOrElse(record, Seq.empty).length
    }

    override def compressData(implicit clock: Clock[IO]): IO[Unit] = ???

    override def compressData(eventType: Int)(implicit clock: Clock[IO]): IO[Seq[Int]] = ???

    override def eventTypes: IO[Seq[Int]] = ???

    override def rawResults(key: Int)(implicit clock: Clock[IO]): IO[(Timestamp, Seq[Int])] =
      ???
  }

  implicit val singleNumberConv: Conv[SingleNumber, Int] = new Conv[SingleNumber, Int] {
    override def conv(raw: SingleNumber): Int = raw.n
  }

  case class SingleNumber(n: Int)

  object SingleNumber {

    implicit val decoder: Decoder[SingleNumber] = deriveDecoder[SingleNumber]
  }
}
