package com.paranid5.tgpostbot
package bot.commands.help

import bot.commands.BotCommand
import utils.telegram.chatId

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

import scala.annotation.targetName

private val Title: String = "/help"

class HelpCommand extends BotCommand[SendResponse]:
  @targetName("matches")
  override def ? (text: String): Boolean =
    text == Title

  override def execute(bot: TelegramBot, message: Message): SendResponse =
    sendHelpCommand(bot, message.chatId)

private def sendHelpCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute SendMessage(chatId, helpCommandText)
