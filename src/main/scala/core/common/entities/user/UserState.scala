package com.paranid5.tgpostbot
package core.common.entities.user

enum UserState(val user: User):
  case None         (override val user: User) extends UserState(user)
  case StartSent    (override val user: User) extends UserState(user)
  case StoreSent    (override val user: User) extends UserState(user)
  case StorePostSent(override val user: User) extends UserState(user)
  case RemoveSent   (override val user: User) extends UserState(user)
  case RemoveIdSent (override val user: User) extends UserState(user)
  case UpdateSent   (override val user: User) extends UserState(user)
  case HelpSent     (override val user: User) extends UserState(user)
