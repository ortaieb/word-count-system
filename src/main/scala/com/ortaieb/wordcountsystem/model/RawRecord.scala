package com.ortaieb.wordcountsystem.model

import com.ortaieb.wordcountsystem.{Data, EventType, Timestamp}
import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

case class RawRecord(eventType: EventType, data: Data, timestamp: Timestamp)

object RawRecord {
  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val rawRecordDecoder: Decoder[RawRecord] = deriveConfiguredDecoder[RawRecord]
}
