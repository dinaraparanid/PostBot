package com.paranid5.tgpostbot
package data.post.message_entity_src

import core.common.entities.post.MessageEntity

import cats.implicits.*

import doobie.ConnectionIO
import doobie.implicits.*

final class PostgresMessageEntityDataSource

object PostgresMessageEntityDataSource:
  given MessageEntityDataSource[ConnectionIO, PostgresMessageEntityDataSource] with
    extension (source: PostgresMessageEntityDataSource)
      override def entities: ConnectionIO[List[MessageEntity]] =
        sql"""SELECT * FROM "MessageEntity""""
          .query[MessageEntity]
          .to[List]

      override def entitiesByPost(postId: Long): ConnectionIO[List[MessageEntity]] =
        sql""" SELECT * FROM "MessageEntity" WHERE post_id = $postId"""
          .query[MessageEntity]
          .to[List]

      override def storeEntity(
        entityType:    String,
        offset:        Int,
        length:        Int,
        url:           Option[String],
        customEmojiId: Option[String],
        postId:        Long,
      ): ConnectionIO[Long] =
        sql"""
        INSERT INTO "MessageEntity" (type, start_offset, length, url, custom_emoji_id, post_id)
        VALUES ($entityType, $offset, $length, $url, $customEmojiId, $postId)
        RETURNING id""".query[Long].unique

      override def updateEntity(
        id:               Long,
        newEntityType:    String,
        newOffset:        Int,
        newLength:        Int,
        newUrl:           Option[String],
        newCustomEmojiId: Option[String],
        newPostId:        Long,
      ): ConnectionIO[Unit] =
        sql"""
        UPDATE "MessageEntity" SET
          type = $newEntityType,
          start_offset = $newOffset,
          length = $newLength,
          url = $newUrl,
          custom_emoji_id = $newCustomEmojiId,
          post_id = $newPostId
        WHERE id = $id""".update.run.map(_ ⇒ ())

      override def updateEntities(
        postId:   Long,
        entities: List[MessageEntity],
      ): ConnectionIO[Unit] =
        for
          _ ← source deleteEntitiesByPost postId
          _ ← source.storeEntities(postId, entities)
        yield ()

      override def deleteEntity(id: Long): ConnectionIO[Unit] =
        sql"""DELETE FROM "MessageEntity" WHERE id = $id""".update.run.map(_ ⇒ ())

      override def deleteEntitiesByPost(postId: Long): ConnectionIO[Unit] =
        sql"""DELETE FROM "MessageEntity" WHERE post_id = $postId""".update.run.map(_ ⇒ ())
