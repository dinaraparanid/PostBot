package com.paranid5.tgpostbot
package core.common.entities.post

final case class MessageEntity(
  id:            Long,
  entityType:    String,
  offset:        Int,
  length:        Int,
  url:           Option[String],
  customEmojiId: Option[String],
  postId:        Long,
)
