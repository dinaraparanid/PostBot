package com.paranid5.tgpostbot
package bot.commands.start

import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onStartCommand[S: UserStateDataSource](
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  for _ <- patchUserStartState(message.botUser, stateSource)
    yield sendStartCommand(bot, message.chatId)

private def sendStartCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute
    SendMessage(chatId, startCommandText)
      .parseMode(ParseMode.Markdown)
