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
  bot:             TelegramBot,
  message:         Message,
  stateSource:     U,
  postsRepository: R
): IO[SendResponse] =
  for
    _   ← patchUserStorePostSentState(message.botUser, stateSource)
    res ← storePostAndRespond(bot, message, postsRepository)
  yield res

private def storePostAndRespond[R: TgPostsRepository](
  bot:             TelegramBot,
  message:         Message,
  postsRepository: R
): IO[SendResponse] =
  for res ← postsRepository storePost message
    yield res.fold(
      e  ⇒ bot execute SendMessage(message.chatId, e.getMessage),
      id ⇒ bot execute SendMessage(message.chatId, postReceivedText(id))
    )
