package com.paranid5.tgpostbot
package data.post

import cats.effect.IO

import doobie.Transactor
import doobie.util.transactor

import io.github.cdimascio.dotenv.Dotenv

private val PostgresDbUrl      = "POSTGRES_DB_URL"
private val PostgresDbUser     = "POSTGRES_DB_USER"
private val PostgresDbPassword = "POSTGRES_DB_PASSWORD"

final class PostgresTgPostsRepository(
  private val postDataSource: PostgresPostDataSource,
  private val userDataSource: PostgresUserDataSource
)

object PostgresTgPostsRepository:
  given postgresTgPostRepository: TgPostsRepository[
    PostgresTgPostsRepository,
    PostgresPostDataSource,
    PostgresUserDataSource
  ] with
    override protected def postDataSource(transactor: IOTransactor): PostgresPostDataSource =
      PostgresPostDataSource(transactor)

    override protected def userDataSource(transactor: IOTransactor): PostgresUserDataSource =
      PostgresUserDataSource(transactor)

    override def connect(dotenv: Dotenv): (PostgresTgPostsRepository, IOTransactor) =
      val transactor = Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = dotenv get PostgresDbUrl,
        user = dotenv get PostgresDbUser,
        password = dotenv get PostgresDbPassword,
        logHandler = None
      )

      val rep = PostgresTgPostsRepository(
        postDataSource = postDataSource(transactor),
        userDataSource = userDataSource(transactor)
      )

      (rep, transactor)
