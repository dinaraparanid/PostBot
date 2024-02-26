package com.paranid5.tgpostbot
package core.common.entities.user

import io.circe.Encoder.AsObject
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import io.circe.`export`.Exported

case class UserId(value: Long) derives Decoder

object UserId:
  given Exported[AsObject[User]] =
    deriveEncoder[User]
  
  given Conversion[Long, UserId] with
    extension (x: Long)
      override def convert: UserId =
        UserId(x)

    override def apply(x: Long): UserId = UserId(x)
