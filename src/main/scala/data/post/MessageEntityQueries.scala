package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.MessageEntity

import cats.implicits.*

import doobie.ConnectionIO
import doobie.implicits.*

case object MessageEntityQueries:
  def entities: ConnectionIO[List[MessageEntity]] =
    sql"""SELECT * FROM "MessageEntity""""
      .query[MessageEntity]
      .to[List]

  def entitiesByPost(postId: Long): ConnectionIO[List[MessageEntity]] =
    sql""" SELECT * FROM "MessageEntity" WHERE post_id = $postId"""
      .query[MessageEntity]
      .to[List]

  def storeEntity(
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

  private def storeEntity(postId: Long)(entity: MessageEntity): ConnectionIO[Long] =
    storeEntity(
      entityType    = entity.entityType,
      offset        = entity.offset,
      length        = entity.offset,
      url           = entity.url,
      customEmojiId = entity.customEmojiId,
      postId        = postId
    )

  def storeEntities(entities: List[MessageEntity])(postId: Long): ConnectionIO[List[Long]] =
    entities
      .map(MessageEntityQueries storeEntity postId)
      .sequence

  def updateEntity(
    id:               Long,
    newEntityType:    String,
    newOffset:        Int,
    newLength:        Int,
    newUrl:           Option[String],
    newCustomEmojiId: Option[String],
    newPostId:        Long,
  ): ConnectionIO[Int] =
    sql"""
    UPDATE "MessageEntity" SET
      type = $newEntityType,
      start_offset = $newOffset,
      length = $newLength,
      url = $newUrl,
      custom_emoji_id = $newCustomEmojiId,
      post_id = $newPostId
    WHERE id = $id""".update.run

  def updateEntities(
    entities: List[MessageEntity],
    postId:   Long
  ): ConnectionIO[Unit] =
    for
      _ ← MessageEntityQueries deleteEntitiesByPost postId
      _ ← storeEntities(entities)(postId)
    yield ()

  def deleteEntity(id: Long): ConnectionIO[Int] =
    sql"""DELETE FROM "MessageEntity" WHERE id = $id""".update.run

  def deleteEntitiesByPost(postId: Long): ConnectionIO[Int] =
    sql"""DELETE FROM "MessageEntity" WHERE post_id = $postId""".update.run
