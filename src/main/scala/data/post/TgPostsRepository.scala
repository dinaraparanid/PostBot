package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.{MessageEntity, PostWithEntities}
import core.common.entities.user.User

import cats.effect.IO

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

    def updatePost(
      id:          Long,
      user:        User,
      chatId:      Long,
      newDate:     Int,
      newText:     String,
      newEntities: List[MessageEntity]
    ): IO[Either[Throwable, Unit]]

    def removePost(id: Long): IO[Either[Throwable, Unit]]
