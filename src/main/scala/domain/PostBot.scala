package com.paranid5.tgpostbot
package domain

import utils.waitForEternity

import cats.effect.IO
import cats.effect.std.{Dispatcher, Queue}

import com.pengrad.telegrambot.model.Message
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

    _ ← launchBotEventLoop(messageQueue).start
  yield ()

private def launchBotEventLoop(messageQueue: Queue[IO, Message]): IO[Unit] =
  def impl(): IO[Unit] =
    for message ← messageQueue.take
      yield println(message.text())

  impl().foreverM