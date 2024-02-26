package com.paranid5.tgpostbot
package bot.commands

import start.StartCommand.startHandler
import help.HelpCommand.helpHandler
import store.StoreCommand.storeHandler

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.SendResponse

private val StartCommand = "/start"
private val HelpCommand = "/help"
private val StoreCommand = "/store"

def handleCommand(bot: TelegramBot, message: Message): SendResponse =
  message.text() match
    case StartCommand ⇒ startHandler.execute(bot, message)
    case HelpCommand  ⇒ helpHandler.execute(bot, message)
    case StoreCommand ⇒ storeHandler.execute(bot, message)
