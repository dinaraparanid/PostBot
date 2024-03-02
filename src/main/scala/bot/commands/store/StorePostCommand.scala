package com.paranid5.tgpostbot
package bot.commands.store

import data.post.TgPostsRepository
import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onStorePostCommand[U: UserStateDataSource, R: TgPostsRepository](
  message:         Message,
  bot:             TelegramBot,
  stateSource:     U,
  postsRepository: R
): IO[SendResponse] =
  for
    _   ← patchUserStorePostSentState(message.botUser, stateSource)
    res ← storePostAndRespond(message, bot, postsRepository)
  yield res

private def storePostAndRespond[R: TgPostsRepository](
  message:         Message,
  bot:             TelegramBot,
  postsRepository: R
): IO[SendResponse] =
  for res ← postsRepository storePost message
    yield res.fold(
      fa = { e ⇒ bot execute SendMessage(message.chatId, e.getMessage) },
      fb = { _ ⇒ bot execute SendMessage(message.chatId, postReceivedText) }
    )
