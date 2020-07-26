package com.ortaieb.wordcountsystem.model

import com.ortaieb.wordcountsystem.{Data, EventType, Timestamp}
import io.circe.Encoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

case class WcResponse(eventType: EventType, timestamp: Timestamp, count: Map[Data, Int])

object WcResponse {

  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val wcResponseEncoder: Encoder[WcResponse] = deriveConfiguredEncoder[WcResponse]

  implicit def wcResponseEntityEncoder[F[_]]: EntityEncoder[F, WcResponse] =
    jsonEncoderOf[F, WcResponse]
}
