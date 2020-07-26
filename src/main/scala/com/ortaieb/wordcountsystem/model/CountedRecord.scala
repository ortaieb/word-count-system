package com.ortaieb.wordcountsystem.model

import com.ortaieb.wordcountsystem.{Data, EventType, Timestamp}
import io.circe.Encoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

case class CountedRecord(eventType: EventType, data: Data, timestamp: Timestamp, cnt: Int) {
  val unique: (EventType, Data, Timestamp) = (eventType, data, timestamp)
}

object CountedRecord {
  def apply(rawRecord: RawRecord): CountedRecord =
    CountedRecord(rawRecord.eventType, rawRecord.data, rawRecord.timestamp, 1)

  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val countedRecordEncoder: Encoder[CountedRecord] = deriveConfiguredEncoder[CountedRecord]

  implicit def countedRecordSeqEntityEncoder[F[_]]: EntityEncoder[F, Seq[CountedRecord]] =
    jsonEncoderOf[F, Seq[CountedRecord]]

}
