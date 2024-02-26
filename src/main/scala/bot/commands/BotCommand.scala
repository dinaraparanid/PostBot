package com.paranid5.tgpostbot
package bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message

import scala.annotation.targetName

trait BotCommand[-T, +R]:
  def execute(bot: TelegramBot, message: Message): R
