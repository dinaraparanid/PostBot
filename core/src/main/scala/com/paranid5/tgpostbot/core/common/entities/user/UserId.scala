package com.paranid5.tgpostbot.core.common.entities.user

import scala.language.implicitConversions

case class UserId(value: Long)

object UserId:
  implicit def longToUserId(value: Long): UserId =
    UserId(value)
