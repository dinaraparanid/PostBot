package com.paranid5.tgpostbot
package data.post

import utils.telegram.userId

import cats.effect.IO

import com.pengrad.telegrambot.model.Message

import doobie.Transactor

trait PostDataSource[S]:
  extension (source: S)
    def storePost(
      userId:     Long,
      date:       Int,
      text:       String,
    ): IO[Long]

    def storePost(message: Message): IO[Long] =
      source.storePost(
        userId = message.userId.value,
        date = message.date,
        text = message.text,
      )

    def updatePost(
      id:         Long,
      newUserId:  Long,
      newDate:    Int,
      newText:    String,
    ): IO[Unit]

    def updatePost(id: Long, message: Message): IO[Unit] =
      source.updatePost(
        id = id,
        newUserId = message.userId.value,
        newDate = message.date,
        newText = message.text
      )

    def deletePost(id: Long): IO[Unit]
