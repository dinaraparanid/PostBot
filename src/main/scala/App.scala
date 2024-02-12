package com.paranid5.tgpostbot

import config.botToken
import domain.launchPostBot
import utils.waitForEternity

import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:
  val run: IO[Unit] =
    for {
      _ ← launchPostBot(botToken)
      _ ← waitForEternity()
    } yield ()
