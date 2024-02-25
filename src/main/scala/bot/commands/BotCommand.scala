package com.paranid5.tgpostbot
package bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message

import scala.annotation.targetName

trait BotCommand[+R]:
  @targetName("matches")
  def ? (text: String): Boolean

  def execute(bot: TelegramBot, message: Message): R
