package com.paranid5.tgpostbot
package core.common.entities.user

import io.circe.Decoder

sealed trait UserState:
  def user: User

case class NoneState(override val user: User) extends UserState derives Decoder
case class StartSentState(override val user: User) extends UserState derives Decoder
case class StoreSentState(override val user: User) extends UserState derives Decoder
case class StorePostSentState(override val user: User) extends UserState derives Decoder
case class RemoveSentState(override val user: User) extends UserState derives Decoder
case class UpdateSentState(override val user: User) extends UserState derives Decoder
case class HelpSentState(override val user: User) extends UserState derives Decoder
