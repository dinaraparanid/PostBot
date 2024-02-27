package com.paranid5.tgpostbot
package bot

import bot.commands.handleCommand
import data.user.user_state.RedisUserStateDataSource.redisDataSource
import data.user.user_state.UserStateDataSource
import utils.waitForEternity

import cats.effect.IO
import cats.effect.std.{Dispatcher, Queue}

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.SendResponse
import com.pengrad.telegrambot.{TelegramBot, UpdatesListener}

import scala.jdk.CollectionConverters.given

def launchPostBot(token: String): IO[Unit] =
  val bot = TelegramBot(token)

  def impl(
    dispatcher:   Dispatcher[IO],
    messageQueue: Queue[IO, Message]
  ): Unit =
    bot setUpdatesListener: updates ⇒
      updates.asScala
        .map(_.message())
        .foreach: msg ⇒
          dispatcher.unsafeRunSync(messageQueue offer msg)

      UpdatesListener.CONFIRMED_UPDATES_ALL

  for
    messageQueue ← Queue.unbounded[IO, Message]

    _ ← Dispatcher
      .sequential[IO]
      .use: dispatcher ⇒
        for
          _ ← IO(impl(dispatcher, messageQueue)).start
          _ ← waitForEternity()
        yield ()
      .start

    stateSource ← redisDataSource.launch
    _           ← launchBotEventLoop(bot, messageQueue, stateSource).start
  yield ()

private def launchBotEventLoop[S: UserStateDataSource](
  bot:          TelegramBot,
  messageQueue: Queue[IO, Message],
  stateSource:  S
): IO[Unit] =
  def impl: IO[SendResponse] =
    for
      message  ← messageQueue.take
      response ← handleCommand(bot, message, stateSource)
    yield response

  impl.foreverM