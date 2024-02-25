package com.paranid5.tgpostbot
package bot.commands.start

import bot.commands.BotCommand
import utils.telegram.chatId

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

import scala.annotation.targetName

private val Title: String = "/start"

class StartCommand extends BotCommand[SendResponse]:
  @targetName("matches")
  override def ? (text: String): Boolean =
    text == Title

  override def execute(bot: TelegramBot, message: Message): SendResponse =
    sendStartCommand(bot, message.chatId)

private def sendStartCommand(
  bot:    TelegramBot,
  chatId: Long
): SendResponse =
  bot execute
    SendMessage(chatId, startCommandText)
      .parseMode(ParseMode.Markdown)
