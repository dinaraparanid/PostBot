package com.paranid5.tgpostbot
package utils

import cats.effect.IO

def waitForEternity(): IO[Unit] =
  for
    closeEffect ← IO(()).foreverM.start
    _ ← closeEffect.join
  yield ()
