package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.{MessageEntity, PostWithEntities}
import core.common.entities.user.User
import utils.telegram.*

import cats.effect.IO

import com.pengrad.telegrambot.model.Message

import io.github.cdimascio.dotenv.Dotenv

trait TgPostsRepository[R]:
  def connect(dotenv: Dotenv): R

  extension (repository: R)
    def getPostsByUser(user: User): IO[List[PostWithEntities]]

    def storePost(
      user:     User,
      date:     Int,
      text:     String,
      chatId:   Long,
      entities: List[MessageEntity]
    ): IO[Either[Throwable, Long]]

    def storePost(message: Message): IO[Either[Throwable, Long]] =
      repository.storePost(
        user     = message.botUser,
        date     = message.date,
        text     = message.text,
        chatId   = message.chatId,
        entities = message.postEntities
      )

    def updatePost(
      id:          Long,
      user:        User,
      chatId:      Long,
      newDate:     Int,
      newText:     String,
      newEntities: List[MessageEntity]
    ): IO[Either[Throwable, Unit]]

    def updatePost(id: Long, message: Message): IO[Either[Throwable, Unit]] =
      repository.updatePost(
        id          = id,
        user        = message.botUser,
        chatId      = message.chatId,
        newDate     = message.date,
        newText     = message.text,
        newEntities = message.postEntities
      )

    def removePost(id: Long): IO[Either[Throwable, Unit]]
