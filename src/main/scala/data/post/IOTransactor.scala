package com.paranid5.tgpostbot
package data.post

import cats.effect.IO
import doobie.util.transactor

type IOTransactor = transactor.Transactor.Aux[IO, Unit]
