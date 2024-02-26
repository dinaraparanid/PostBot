package com.paranid5.tgpostbot
package bot.commands.store

import bot.commands.BotCommand
import utils.telegram.chatId

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

final class StoreCommand

object StoreCommand:
  given storeHandler: BotCommand[StoreCommand, SendResponse] with
    override def execute(bot: TelegramBot, message: Message): SendResponse =
      sendRequestPost(bot, message.chatId)
    
private def sendRequestPost(
  bot: TelegramBot,
  chatId: Long
): SendResponse =
  bot execute
    SendMessage(chatId, requestPostText)
