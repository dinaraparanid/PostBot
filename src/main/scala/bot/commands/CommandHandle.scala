package com.paranid5.tgpostbot
package bot.commands

import bot.commands.help.*
import bot.commands.start.*
import bot.commands.store.*
import bot.commands.unknown.onUnknownCommand
import core.common.entities.user.UserState
import data.user.user_state.UserStateDataSource
import utils.telegram.*

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.SendResponse

private val StartCommand = "/start"
private val HelpCommand  = "/help"
private val StoreCommand = "/store"

def handleCommand[S: UserStateDataSource](
  bot:         TelegramBot,
  message:     Message,
  stateSource: S
): IO[SendResponse] =
  def impl(userState: UserState) =
    (message.text(), userState) match
      case (StartCommand, _) ⇒
        onStartCommand(bot, message, stateSource)

      case (HelpCommand, _) ⇒
        onHelpCommand(bot, message, stateSource)

      case (StoreCommand, _) ⇒
        onStoreCommand(bot, message, stateSource)

      case (post, UserState.StoreSent(_)) ⇒
        onStorePostCommand(post, bot, message, stateSource)

      case (command, _) ⇒
        onUnknownCommand(command, bot, message)

  for
    userState ← stateSource.userStateSource(message.botUser)
    result    ← impl(userState)
  yield result