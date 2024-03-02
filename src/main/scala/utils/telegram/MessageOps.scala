package com.paranid5.tgpostbot
package utils.telegram

import core.common.entities.user.{User, UserId}

import com.paranid5.tgpostbot.core.common.entities.post.MessageEntity
import com.pengrad.telegrambot.model.Message

extension (message: Message)
  def userId: UserId =
    UserId(message.from.id)

  def chatId: Long =
    message.chat.id

  def botUser: User =
    val from = message.from

    User(
      id = from.id,
      chatId = message.chatId,
      userName = from.username,
      firstName = Option(from.firstName),
      lastName = Option(from.lastName)
    )

  def postEntities: List[MessageEntity] =
    Option(message.entities)
      .map(_.toList)
      .getOrElse(Nil)
      .map(new MessageEntity(_))
    
