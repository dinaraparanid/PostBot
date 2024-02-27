package com.paranid5.tgpostbot
package bot.commands.unknown

import utils.telegram.chatId

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onUnknownCommand(
  command: String,
  bot:     TelegramBot,
  message: Message,
): IO[SendResponse] =
  IO pure sendUnknownCommand(command, bot, message.chatId)

private def sendUnknownCommand(
  command: String,
  bot:     TelegramBot,
  chatId:  Long
): SendResponse =
  bot execute SendMessage(chatId, unknownText(command))
