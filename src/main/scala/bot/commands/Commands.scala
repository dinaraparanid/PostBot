package com.paranid5.tgpostbot
package bot.commands

import bot.commands.help.HelpCommand
import bot.commands.start.StartCommand
import bot.commands.store.StoreCommand

import com.pengrad.telegrambot.response.SendResponse

def commands: List[BotCommand[SendResponse]] =
  List(
    StartCommand(),
    HelpCommand(),
    StoreCommand()
  )
