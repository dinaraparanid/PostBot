package com.paranid5.tgpostbot
package data.post

import cats.effect.IO
import com.pengrad.telegrambot.model.Message

trait UserDataSource[S]:
  extension (source: S)
    def storeUser(
      id:        Long,
      chatId:    Long,
      userName:  String,
      firstName: Option[String],
      lastName:  Option[String],
    ): IO[Unit]

    def updateUser(
      id:           Long,
      newChatId:    Long,
      newUserName:  String,
      newFirstName: String,
      newLastName:  String
    ): IO[Unit]

    def deleteUser(id: Long): IO[Unit]
