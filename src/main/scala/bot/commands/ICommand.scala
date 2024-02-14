package com.paranid5.tgpostbot
package bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message

import scala.annotation.targetName

case class ICommand[C, +R](c: C, command: BotCommand[C, R]):
  @targetName("matches")
  def ? (text: String): Boolean =
    command.?(c)(text)

  def execute(bot: TelegramBot, message: Message): R =
    command.execute(c)(bot, message)
