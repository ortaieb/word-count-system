package com.ortaieb.wordcountsystem

import cats.effect.{Clock, IO}
import com.ortaieb.wordcountsystem.model.CountedRecord
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class InMemoryStoreSpec extends AnyWordSpec with Matchers with InMemoryStoreFixture {

  "InMemoryStore" when {
    "append" must {
      "return incremented value of records against the eventType" in {
        val store = new InMemoryStore(10.seconds)
        val input = CountedRecord("A", "a", 0L, 1)

        store.append(input).unsafeRunSync() mustBe 1
      }
    }

    implicit val clock: Clock[IO] = dummyClock100

    "compressData" must {
      "return empty if store entry does not exist" in {
        val store = new InMemoryStore(15.seconds, Map.empty)
        store.compressData("A")(clock).unsafeRunSync() mustBe empty
      }

      "return added count records" in {
        val store = new InMemoryStore(15.seconds, initData)
        store.compressData("A")(clock).unsafeRunSync() mustBe compressedData("A")
      }
    }
    "eventTypes" must {
      "return list of event types currently sourced" in {
        val store = new InMemoryStore(15.seconds, initData)
        store.eventTypes.unsafeRunSync() must contain theSameElementsAs Seq("A", "B")
      }
      "return empty list of no entries stored" in {
        val store = new InMemoryStore(15.seconds)
        store.eventTypes.unsafeRunSync() mustBe empty
      }
    }
    "rawResults" must {
      "return filtered uncompressed results" in {
        val store = new InMemoryStore(15.seconds, initData)
        val (ts, log) = store.rawResults("B")(clock).unsafeRunSync()
        ts mustBe 100
        log must contain theSameElementsAs compressedData("B")
      }
    }
  }
}

trait InMemoryStoreFixture {

  val dummyClock100: Clock[IO] = new Clock[IO] {
    override def realTime(unit: TimeUnit): IO[Timestamp] = IO.pure(100L)
    override def monotonic(unit: TimeUnit): IO[Timestamp] = IO.pure(100L)
  }

  val initData: Map[EventType, Seq[CountedRecord]] = Map(
    "A" -> Seq(
      CountedRecord("A", "a", 99, 1),
      CountedRecord("A", "a", 79, 1),
      CountedRecord("A", "a", 99, 1)
    ),
    "B" -> Seq(
      CountedRecord("B", "a", 99, 1),
      CountedRecord("B", "a", 99, 2)
    )
  )

  val compressedData: Map[EventType, Seq[CountedRecord]] = Map(
    "A" -> Seq(CountedRecord("A", "a", 99, 2)),
    "B" -> Seq(CountedRecord("B", "a", 99, 2), CountedRecord("B", "a", 99, 1))
  )
}
