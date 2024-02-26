package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.{UserId, UserState}

sealed trait Request
final case class GetUserState(id: UserId) extends Request
final case class PatchUserState(userState: UserState) extends Request
