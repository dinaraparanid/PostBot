package com.paranid5.tgpostbot
package data.post

import cats.effect.IO

import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor

final class PostgresUserDataSource(
  private val transactor: Transactor.Aux[IO, Unit]
)

object PostgresUserDataSource:
  given UserDataSource[PostgresUserDataSource] with
    extension (source: PostgresUserDataSource)
      override def storeUser(
        id: Long,
        chatId: Long,
        userName: String,
        firstName: Option[String],
        lastName: Option[String]
      ): IO[Unit] =
        val query = sql"""
                    INSERT INTO User(id, first_name, last_name, username, chat_id)
                    VALUES ($id, ${firstName.orNull}, ${lastName.orNull}, $userName, $chatId)""".update

        (query.run transact source.transactor).void

      override def updateUser(
        id:           Long,
        newChatId:    Long,
        newUserName:  String,
        newFirstName: String,
        newLastName:  String
      ): IO[Unit] =
        val query = sql"""
                    UPDATE User SET chat_id = $newChatId, username = $newUserName, first_name = $newFirstName, last_name = $newLastName
                    WHERE id = $id""".update

        (query.run transact source.transactor).void

      override def deleteUser(id: Long): IO[Unit] =
        val query = sql"DELETE FROM User WHERE id = $id".update
        (query.run transact source.transactor).void