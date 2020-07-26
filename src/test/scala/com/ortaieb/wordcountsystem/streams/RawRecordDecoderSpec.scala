package com.ortaieb.wordcountsystem.streams

import com.ortaieb.wordcountsystem.model.CountedRecord
import fs2.Stream
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RawRecordDecoderSpec extends AnyWordSpec with Matchers {

  val test = RawRecordDecoder.countedRecordIoDecoder

  "RawRecordDecoder" must {
    "reject invalid string and return None" in {
      Stream("invalid jsone").through(test.decodeStep).compile.toList.unsafeRunSync() mustBe empty
    }
    "parse valid string input RawRecord instance" in {
      val input = """{ "event_type": "foo", "data": "bar", "timestamp": 0}"""
      val expected = CountedRecord("foo", "bar", 0L, 1)

      Stream(input).through(test.decodeStep).compile.toList.unsafeRunSync() mustBe List(expected)
    }

  }
}
