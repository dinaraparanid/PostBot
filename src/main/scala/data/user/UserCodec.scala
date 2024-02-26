package com.paranid5.tgpostbot
package data.user

import core.common.entities.user.*

import cats.syntax.all.*

import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

object UserCodec:
  given Encoder[UserId] =
    Encoder.forProduct1("value")(_.value)

  given Encoder[User] =
    Encoder.forProduct4("id", "chatId", "firstName", "lastName"): u ⇒
      (u.id, u.chatId, u.firstName, u.lastName)

  given Encoder[NoneState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[StartSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[StoreSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[StorePostSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[RemoveSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[UpdateSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[HelpSentState] =
    Encoder.forProduct1("user")(_.user)

  given Encoder[UserState] =
    Encoder.instance[UserState]:
      case none @ NoneState(_) ⇒ none.asJson
      case startSent @ StartSentState(_) ⇒ startSent.asJson
      case storeSent @ StoreSentState(_) ⇒ storeSent.asJson
      case storePostSent @ StorePostSentState(_) ⇒ storePostSent.asJson
      case removeSent @ RemoveSentState(_) ⇒ removeSent.asJson
      case updateSent @ UpdateSentState(_) ⇒ updateSent.asJson
      case helpSent @ HelpSentState(_) ⇒ helpSent.asJson

  given Decoder[UserState] =
    List[Decoder[UserState]](
      Decoder[NoneState].widen,
      Decoder[StartSentState].widen,
      Decoder[StoreSentState].widen,
      Decoder[StorePostSentState].widen,
      Decoder[RemoveSentState].widen,
      Decoder[UpdateSentState].widen,
      Decoder[HelpSentState].widen
    ).reduceLeft(_ or _)

