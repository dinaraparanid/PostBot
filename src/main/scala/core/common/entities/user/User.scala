package com.paranid5.tgpostbot
package core.common.entities.user

final case class User(
  id:        Long,
  chatId:    Long,
  userName:  String,
  firstName: Option[String],
  lastName:  Option[String]
)
