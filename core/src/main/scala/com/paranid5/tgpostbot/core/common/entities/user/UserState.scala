package com.paranid5.tgpostbot.core.common.entities.user

sealed trait UserState:
  def user: User

  case class NoneState(override val user: User) extends UserState
  case class StartSentState(override val user: User) extends UserState
  case class StoreSentState(override val user: User) extends UserState
  case class StorePostSentState(override val user: User) extends UserState
  case class RemoveSentState(override val user: User) extends UserState
  case class UpdateSentState(override val user: User) extends UserState
  case class HelpSentState(override val user: User) extends UserState
