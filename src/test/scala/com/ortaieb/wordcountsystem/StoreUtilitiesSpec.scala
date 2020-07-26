package com.ortaieb.wordcountsystem

import com.ortaieb.wordcountsystem.model.CountedRecord
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

/**
  */
class StoreUtilitiesSpec extends AnyWordSpec with Matchers with StoreUtilitiesFixture {

  "StoreUtilities" when {
    "filterStale" must {
      "remove any older than window long messages" in {
        StoreUtilities.filterStale(now, staleData ++ recentData)(window) mustBe recentData
      }

      "return empty sequence if all stale" in {
        StoreUtilities.filterStale(now, staleData)(window) mustBe empty
      }
    }
    "group" must {
      "return compressed records representing the same number of records" in {
        StoreUtilities.group(rawDataToGroup) must contain theSameElementsAs grouped
      }
    }
    "wordCount" must {
      "return a map of data (word) with number of fresh instances" in {
        StoreUtilities.wordsCount(grouped) mustBe wc
      }
    }
  }
}

trait StoreUtilitiesFixture {
  val now = 10.minutes.toMillis
  val window = 2.minute

  val expectedThreshold = now - window.toSeconds

  val recentData = Seq(
    CountedRecord("A", "a", expectedThreshold + 1, 1)
  )
  val staleData = Seq(
    CountedRecord("A", "b", expectedThreshold - 100, 1),
    CountedRecord("A", "c", expectedThreshold - 1, 1)
  )

  val rawDataToGroup = Seq(
    CountedRecord("A", "a", expectedThreshold + 1, 1),
    CountedRecord("A", "a", expectedThreshold + 1, 1),
    CountedRecord("A", "a", expectedThreshold + 2, 1),
    CountedRecord("A", "b", expectedThreshold + 2, 1),
    CountedRecord("A", "a", expectedThreshold + 2, 1)
  )

  val grouped = Seq(
    CountedRecord("A", "a", expectedThreshold + 1, 2),
    CountedRecord("A", "a", expectedThreshold + 2, 2),
    CountedRecord("A", "b", expectedThreshold + 2, 1)
  )

  val wc: Map[Data, Int] = Map("a" -> 4, "b" -> 1)
}
