package com.ortaieb

import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object wordcountsystem {
  type EventType = String
  type Data = String
  type Timestamp = Long

  implicit def eventTypeSeqEntityEncoder[F[_]]: EntityEncoder[F, Seq[EventType]] =
    jsonEncoderOf[F, Seq[EventType]]
}
