package com.paranid5.tgpostbot
package bot.commands.help

import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onHelpCommand[S: UserStateDataSource](
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  for _ <- patchUserHelpSentState(message.botUser, stateSource)
    yield sendHelpCommand(bot, message.chatId)

private def sendHelpCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute SendMessage(chatId, helpCommandText)
