package com.paranid5.tgpostbot
package data.post.user_src

import core.common.entities.user.User

import doobie.ConnectionIO
import doobie.implicits.*

final class PostgresUserDataSource

object PostgresUserDataSource:
  given UserDataSource[ConnectionIO, PostgresUserDataSource] with
    extension (source: PostgresUserDataSource)
      override def users: ConnectionIO[List[User]] =
        sql"""SELECT * FROM "User"""".query[User].to[List]

      override def userById(id: Long): ConnectionIO[Option[User]] =
        sql"""SELECT * FROM "User" WHERE id = $id""".query[User].option

      override def storeOrUpdateUser_(
        id:        Long,
        chatId:    Long,
        userName:  String,
        firstName: Option[String],
        lastName:  Option[String]
      ): ConnectionIO[Int] =
        sql"""
        INSERT INTO "User" (id, first_name, last_name, username, chat_id)
        VALUES ($id, $firstName, $lastName, $userName, $chatId)
        ON CONFLICT (id) DO UPDATE
        SET first_name = $firstName,
            last_name = $lastName,
            username = $userName,
            chat_id  = $chatId""".update.run

      override def updateUser_(
        id:           Long,
        newChatId:    Long,
        newUserName:  String,
        newFirstName: Option[String],
        newLastName:  Option[String]
      ): ConnectionIO[Unit] =
        sql"""
        UPDATE "User" SET
          chat_id = $newChatId,
          username = $newUserName,
          first_name = $newFirstName,
          last_name = $newLastName
        WHERE id = $id""".update.run.map(_ ⇒ ())

      override def deleteUser(id: Long): ConnectionIO[Unit] =
        sql"""DELETE FROM "User" WHERE id = $id""".update.run.map(_ ⇒ ())
