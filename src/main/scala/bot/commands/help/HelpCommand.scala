package com.paranid5.tgpostbot
package bot.commands.help

import bot.commands.BotCommand
import utils.telegram.chatId

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

final class HelpCommand

object HelpCommand:
  given helpHandler: BotCommand[HelpCommand, SendResponse] with
    override def execute(bot: TelegramBot, message: Message): SendResponse =
      sendHelpCommand(bot, message.chatId)

private def sendHelpCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute SendMessage(chatId, helpCommandText)
