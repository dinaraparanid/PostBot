package com.paranid5.tgpostbot
package bot.commands

import bot.commands.help.*
import bot.commands.remove.*
import bot.commands.start.*
import bot.commands.store.*
import bot.commands.unknown.onUnknownCommand
import core.common.entities.user.UserState
import data.post.TgPostsRepository
import data.user.user_state.UserStateDataSource
import utils.telegram.*

import cats.effect.IO
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.SendResponse

private val StartCommand = "/start"
private val HelpCommand  = "/help"
private val StoreCommand = "/store"
private val RemoveCommand = "/remove"

def handleCommand[U: UserStateDataSource, R: TgPostsRepository](
  bot:             TelegramBot,
  message:         Message,
  stateSource:     U,
  postsRepository: R
): IO[SendResponse] =
  def impl(userState: UserState) =
    (message.text(), userState) match
      case (StartCommand, _) ⇒
        onStartCommand(bot, message, stateSource)

      case (HelpCommand, _) ⇒
        onHelpCommand(bot, message, stateSource)

      case (StoreCommand, _) ⇒
        onStoreCommand(bot, message, stateSource)

      case (_, UserState.StoreSent(_)) ⇒
        onStorePostCommand(bot, message, stateSource, postsRepository)

      case (RemoveCommand, _) ⇒
        onRemoveCommand(bot, message, stateSource)

      case (_, UserState.RemoveSent(_)) ⇒
        onRemoveByIdCommand(bot, message, stateSource, postsRepository)

      case (command, _) ⇒
        onUnknownCommand(command, bot, message)

  for
    userState ← stateSource.userStateSource(message.botUser)
    result    ← impl(userState)
  yield result