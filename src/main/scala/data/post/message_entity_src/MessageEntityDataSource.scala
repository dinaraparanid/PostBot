package com.paranid5.tgpostbot
package data.post.message_entity_src

import cats.Applicative
import cats.implicits.*

import core.common.entities.post.MessageEntity

trait MessageEntityDataSource[F[_] : Applicative, S]:
  extension (source: S)
    def entities: F[List[MessageEntity]]

    def entitiesByPost(postId: Long): F[List[MessageEntity]]

    def storeEntity(
      entityType:    String,
      offset:        Int,
      length:        Int,
      url:           Option[String],
      customEmojiId: Option[String],
      postId:        Long,
    ): F[Long]

    def storeEntity(postId: Long, entity: MessageEntity): F[Long] =
      source.storeEntity(
        entityType    = entity.entityType,
        offset        = entity.offset,
        length        = entity.offset,
        url           = entity.url,
        customEmojiId = entity.customEmojiId,
        postId        = postId
      )

    def storeEntities(postId: Long, entities: List[MessageEntity]): F[List[Long]] =
      entities
        .map(source.storeEntity(postId, _))
        .sequence

    def updateEntity(
      id:               Long,
      newEntityType:    String,
      newOffset:        Int,
      newLength:        Int,
      newUrl:           Option[String],
      newCustomEmojiId: Option[String],
      newPostId:        Long,
    ): F[Unit]

    def updateEntity(postId: Long, entity: MessageEntity): F[Unit] =
      source.updateEntity(
        id               = entity.id,
        newEntityType    = entity.entityType,
        newOffset        = entity.offset,
        newLength        = entity.length,
        newUrl           = entity.url,
        newCustomEmojiId = entity.customEmojiId,
        newPostId        = postId
      )

    def updateEntities(
      postId:   Long,
      entities: List[MessageEntity],
    ): F[Unit] =
      entities
        .map(source.updateEntity(postId, _))
        .sequence
        .map(_ ⇒ ())

    def deleteEntity(id: Long): F[Unit]

    def deleteEntitiesByPost(postId: Long): F[Unit]
