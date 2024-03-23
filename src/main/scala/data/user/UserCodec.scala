package com.paranid5.tgpostbot
package data.user

import core.common.entities.user.*

import cats.syntax.all.*

import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

object UserCodec:
  given Encoder[UserId] =
    Encoder.forProduct1("value")(_.value)

  given Decoder[UserId] =
    Decoder.forProduct1("value")(UserId.apply)

  given Encoder[User] =
    Encoder.forProduct5("id", "chatId", "userName", "firstName", "lastName"): u ⇒
      (u.id, u.chatId, u.userName, u.firstName, u.lastName)

  given Decoder[User] =
    Decoder.forProduct5("id", "chatId",  "userName", "firstName", "lastName")(User.apply)

  given Encoder[UserState.None] =
    Encoder.forProduct2("None", "user"): s ⇒
      ("None", s.user)

  given Decoder[UserState.None] =
    Decoder.forProduct2("None", "user"): (_: String, u: User) ⇒
      UserState.None(u)

  given Encoder[UserState.StartSent] =
    Encoder.forProduct2("StartSent", "user"): s ⇒
      ("StartSent", s.user)

  given Decoder[UserState.StartSent] =
    Decoder.forProduct2("StartSent", "user"): (_: String, u: User) ⇒
      UserState.StartSent(u)

  given Encoder[UserState.StoreSent] =
    Encoder.forProduct2("StoreSent", "user"): s ⇒
      ("StoreSent", s.user)

  given Decoder[UserState.StoreSent] =
    Decoder.forProduct2("StoreSent", "user"): (_: String, u: User) ⇒
      UserState.StoreSent(u)

  given Encoder[UserState.StorePostSent] =
    Encoder.forProduct2("StorePostSent", "user"): s ⇒
      ("StorePostSent", s.user)

  given Decoder[UserState.StorePostSent] =
    Decoder.forProduct2("StorePostSent", "user"): (_: String, u: User) ⇒
      UserState.StorePostSent(u)

  given Encoder[UserState.RemoveSent] =
    Encoder.forProduct2("RemoveSent", "user"): s ⇒
      ("RemoveSent", s.user)

  given Decoder[UserState.RemoveSent] =
    Decoder.forProduct2("RemoveSent", "user"): (_: String, u: User) ⇒
      UserState.RemoveSent(u)

  given Encoder[UserState.RemoveIdSent] =
    Encoder.forProduct2("RemoveIdSent", "user"): s ⇒
      ("RemoveIdSent", s.user)

  given Decoder[UserState.RemoveIdSent] =
    Decoder.forProduct2("RemoveIdSent", "user"): (_: String, u: User) ⇒
      UserState.RemoveIdSent(u)

  given Encoder[UserState.UpdateSent] =
    Encoder.forProduct2("UpdateSent", "user"): s ⇒
      ("UpdateSent", s.user)

  given Decoder[UserState.UpdateSent] =
    Decoder.forProduct2("UpdateSent", "user"): (_: String, u: User) ⇒
      UserState.UpdateSent(u)

  given Encoder[UserState.HelpSent] =
    Encoder.forProduct2("HelpSent", "user"): s ⇒
      ("HelpSent", s.user)

  given Decoder[UserState.HelpSent] =
    Decoder.forProduct2("HelpSent", "user"): (_: String, u: User) ⇒
      UserState.HelpSent(u)

  given Encoder[UserState] =
    Encoder.instance[UserState]:
      case none          @ UserState.None         (_) ⇒ none.asJson
      case startSent     @ UserState.StartSent    (_) ⇒ startSent.asJson
      case storeSent     @ UserState.StoreSent    (_) ⇒ storeSent.asJson
      case storePostSent @ UserState.StorePostSent(_) ⇒ storePostSent.asJson
      case removeSent    @ UserState.RemoveSent   (_) ⇒ removeSent.asJson
      case removeIdSent  @ UserState.RemoveIdSent (_) ⇒ removeIdSent.asJson
      case updateSent    @ UserState.UpdateSent   (_) ⇒ updateSent.asJson
      case helpSent      @ UserState.HelpSent     (_) ⇒ helpSent.asJson

  given Decoder[UserState] =
    List[Decoder[UserState]](
      Decoder[UserState.None].widen,
      Decoder[UserState.StartSent].widen,
      Decoder[UserState.StoreSent].widen,
      Decoder[UserState.StorePostSent].widen,
      Decoder[UserState.RemoveSent].widen,
      Decoder[UserState.RemoveIdSent].widen,
      Decoder[UserState.UpdateSent].widen,
      Decoder[UserState.HelpSent].widen
    ).reduceLeft(_ or _)
