package com.paranid5.tgpostbot
package bot

import bot.commands.handleCommand
import data.post.PostgresTgPostsRepository.given
import data.post.TgPostsRepository
import data.user.user_state.RedisUserStateDataSource.redisUserDataSource
import data.user.user_state.UserStateDataSource
import utils.waitForEternity

import cats.effect.IO
import cats.effect.std.{Dispatcher, Queue}

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.SendResponse
import com.pengrad.telegrambot.{TelegramBot, UpdatesListener}

import io.github.cdimascio.dotenv.Dotenv

import scala.jdk.CollectionConverters.given

private val BotToken = "BOT_TOKEN"

def launchPostBot(): IO[Unit] =
  def impl(dotenv: Dotenv): IO[Unit] =
    val bot = TelegramBot(dotenv get BotToken)
    val postRepository = postgresTgPostRepository connect dotenv

    for
      stateSource  ← redisUserDataSource.launch
      messageQueue ← launchMessageHandling(bot)
      _            ← launchBotEventLoop(bot, messageQueue, stateSource, postRepository).start
    yield ()

  launchDotenv(impl)

private def launchDotenv(f: Dotenv ⇒ IO[Unit]): IO[Unit] =
  for
    dotenvRes ← IO(Dotenv.load()).attempt
    _         ← dotenvRes.fold(
      fa = IO pure _.printStackTrace,
      fb = f
    )
  yield ()

private def launchMessageHandling(bot: TelegramBot): IO[Queue[IO, Message]] =
  def launchUpdateListener(
    dispatcher:   Dispatcher[IO],
    messageQueue: Queue[IO, Message]
  ): Unit =
    bot setUpdatesListener: updates ⇒
      updates.asScala
        .map(_.message())
        .foreach: msg ⇒
          dispatcher.unsafeRunSync(messageQueue offer msg)

      UpdatesListener.CONFIRMED_UPDATES_ALL

  def impl(messageQueue: Queue[IO, Message]) =
    Dispatcher
      .sequential[IO]
      .use: dispatcher ⇒
        for
          _ ← IO(launchUpdateListener(dispatcher, messageQueue)).start
          _ ← waitForEternity()
        yield ()
      .start

  for
    messageQueue ← Queue.unbounded[IO, Message]
    _            ← impl(messageQueue)
  yield messageQueue

private def launchBotEventLoop[U: UserStateDataSource, R: TgPostsRepository](
  bot:             TelegramBot,
  messageQueue:    Queue[IO, Message],
  stateSource:     U,
  postsRepository: R
): IO[Unit] =
  def impl: IO[SendResponse] =
    for
      message  ← messageQueue.take
      response ← handleCommand(bot, message, stateSource, postsRepository)
    yield response

  impl.foreverM