package com.paranid5.tgpostbot
package bot.commands.store

import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onStoreCommand[S: UserStateDataSource](
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  for _ <- patchUserStoreSentState(message.botUser, stateSource)
    yield sendRequestPost(bot, message.chatId)

private def sendRequestPost(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute SendMessage(chatId, requestPostText)
