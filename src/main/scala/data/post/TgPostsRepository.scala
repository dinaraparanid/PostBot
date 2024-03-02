package com.paranid5.tgpostbot
package data.post

import cats.effect.IO
import doobie.util.transactor
import io.github.cdimascio.dotenv.Dotenv

type IOTransactor = transactor.Transactor.Aux[IO, Unit]

trait TgPostsRepository[R, P: PostDataSource, U: UserDataSource]:
  protected def postDataSource(transactor: IOTransactor): P
  protected def userDataSource(transactor: IOTransactor): U

  def connect(dotenv: Dotenv): (R, IOTransactor)
