package com.paranid5.tgpostbot
package bot.commands.store

import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onStorePostCommand[S: UserStateDataSource](
  post:        String,
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  for _ <- patchUserStorePostSentState(message.botUser, stateSource)
    yield storePostAndRespond(post, bot, message.chatId)

private def storePostAndRespond(
  post:   String,
  bot:    TelegramBot,
  chatId: Long,
): SendResponse =
  println(post)
  bot execute SendMessage(chatId, postReceivedText)
