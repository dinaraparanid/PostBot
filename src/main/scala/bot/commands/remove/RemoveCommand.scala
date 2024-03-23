package com.paranid5.tgpostbot
package bot.commands.remove

import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onRemoveCommand[S: UserStateDataSource](
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  for _ <- patchUserRemoveSentState(message.botUser, stateSource)
    yield sendRemoveCommand(bot, message.chatId)

private def sendRemoveCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute SendMessage(chatId, removeCommandText)