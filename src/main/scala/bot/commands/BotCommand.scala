package com.paranid5.tgpostbot
package bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message

import scala.annotation.targetName

trait BotCommand[-C, +R]:
  extension (c: C)
    @targetName("matches")
    def ? (text: String): Boolean

    def execute(bot: TelegramBot, message: Message): R
