package com.paranid5.tgpostbot
package core.common.entities.user

final case class UserId(value: Long)

object UserId:
  given Conversion[Long, UserId] = UserId(_)
