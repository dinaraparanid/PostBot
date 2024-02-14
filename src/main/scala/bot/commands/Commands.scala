package com.paranid5.tgpostbot
package bot.commands

import bot.commands.help.HelpCommand
import bot.commands.start.StartCommand
import bot.commands.start.StartCommand.given
import bot.commands.help.HelpCommand.given

import com.pengrad.telegrambot.response.SendResponse

extension[C1, R1, C2, R2] (commands: List[ICommand[C1, R1]])
  def addCommand(command: C2)(using bc: BotCommand[C2, R2]): List[ICommand[_ >: C2 with C1, _ >: R1 with R2]] =
    ICommand[C2, R2](command, bc) :: commands

def commands: List[ICommand[? >: StartCommand & HelpCommand, ? >: SendResponse]] =
  List(ICommand(StartCommand(), implicitly[BotCommand[StartCommand, SendResponse]]))
    .addCommand(HelpCommand())
