package com.paranid5.tgpostbot
package utils.telegram

import core.common.entities.user.{User, UserId}

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
