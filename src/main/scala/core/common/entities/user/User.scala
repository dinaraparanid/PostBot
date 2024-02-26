package com.paranid5.tgpostbot
package core.common.entities.user

import io.circe.Encoder.AsObject
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import io.circe.`export`.Exported

case class User(
  id: Long,
  chatId: Long,
  firstName: String,
  lastName: String
) derives Decoder

object User:
  given Exported[AsObject[User]] =
    deriveEncoder[User]
