package com.ortaieb.wordcountsystem.model

import com.ortaieb.wordcountsystem.{Data, EventType, Timestamp}

case class CountedRecord(eventType: EventType, data: Data, timestamp: Timestamp, cnt: Int = 1) {
  val unique: (EventType, Data, Timestamp) = (eventType, data, timestamp)
}

object CountedRecord {

  import io.circe.{Encoder, Decoder}
  import io.circe.generic.extras.Configuration
  import io.circe.generic.extras.semiauto._
  import org.http4s.EntityEncoder
  import org.http4s.circe.jsonEncoderOf

  implicit val jsonConfig: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val countedRecordDecoder: Decoder[CountedRecord] = deriveConfiguredDecoder[CountedRecord]
  implicit val countedRecordEncoder: Encoder[CountedRecord] = deriveConfiguredEncoder[CountedRecord]

  implicit def countedRecordSeqEntityEncoder[F[_]]: EntityEncoder[F, Seq[CountedRecord]] =
    jsonEncoderOf[F, Seq[CountedRecord]]

}
