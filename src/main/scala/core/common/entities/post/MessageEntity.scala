package com.paranid5.tgpostbot
package core.common.entities.post

import com.pengrad.telegrambot.model.MessageEntity as TgEntity

final case class MessageEntity(
  id:            Long,
  entityType:    String,
  offset:        Int,
  length:        Int,
  url:           Option[String],
  customEmojiId: Option[String],
  postId:        Long,
):
  def this(tgEntity: TgEntity, postId: Long = 0L) = this(
    id = 0L,
    entityType = tgEntity.`type`.toString,
    offset = tgEntity.offset,
    length = tgEntity.length,
    url = Option(tgEntity.url),
    customEmojiId = Option(tgEntity.customEmojiId),
    postId = postId
  )