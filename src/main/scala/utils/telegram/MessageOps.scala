package com.paranid5.tgpostbot
package utils.telegram

import com.pengrad.telegrambot.model.Message

extension (message: Message)
  def chatId: Long =
    message.chat().id()
