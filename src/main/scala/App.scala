package com.paranid5.tgpostbot

import bot.launchPostBot
import utils.waitForEternity

import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:
  val run: IO[Unit] =
    for {
      _ ← launchPostBot()
      _ ← waitForEternity()
    } yield ()
