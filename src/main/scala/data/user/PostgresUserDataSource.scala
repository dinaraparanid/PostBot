package com.paranid5.tgpostbot
package data.user

import core.common.entities.user.User

import doobie.*
import doobie.free.connection.ConnectionIO
import doobie.implicits.*

case object PostgresUserDataSource:
  def users: ConnectionIO[List[User]] =
    sql"SELECT * FROM User".query[User].to[List]

  def getUserById(id: Long): ConnectionIO[Option[User]] =
    sql"SELECT * FROM User WHERE id = $id".query[User].option

  def storeOrUpdateUser(
    id:        Long,
    chatId:    Long,
    userName:  String,
    firstName: Option[String],
    lastName:  Option[String]
  ): ConnectionIO[Int] =
    sql"""
    INSERT INTO User(id, first_name, last_name, username, chat_id)
    VALUES ($id, ${firstName.orNull}, ${lastName.orNull}, $userName, $chatId)
    ON CONFLICT (id) DO UPDATE
    SET first_name = ${firstName.orNull},
        last_name = ${lastName.orNull}
        username = $userName,
        chat_id  = $chatId""".update.run

  def storeOrUpdateUser(user: User): ConnectionIO[Int] =
    storeOrUpdateUser(
      id        = user.id,
      chatId    = user.chatId,
      userName  = user.userName,
      firstName = user.firstName,
      lastName  = user.lastName
    )

  def updateUser(
    id:           Long,
    newChatId:    Long,
    newUserName:  String,
    newFirstName: Option[String],
    newLastName:  Option[String]
  ): ConnectionIO[Int] =
    sql"""
    UPDATE User SET
      chat_id = $newChatId,
      username = $newUserName,
      first_name = ${newFirstName.orNull},
      last_name = ${newLastName.orNull}
    WHERE id = $id""".update.run

  def updateUser(user: User): ConnectionIO[Int] =
    updateUser(
      id = user.id,
      newChatId = user.chatId,
      newUserName = user.userName,
      newFirstName = user.firstName,
      newLastName = user.lastName
    )

  def deleteUser(id: Long): ConnectionIO[Int] =
    sql"DELETE FROM User WHERE id = $id".update.run