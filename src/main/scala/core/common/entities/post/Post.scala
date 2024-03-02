package com.paranid5.tgpostbot
package core.common.entities.post

final case class Post(
  id:     Long,
  userId: Long,
  date:   Int,
  text:   String,
  chatId: Long
)
