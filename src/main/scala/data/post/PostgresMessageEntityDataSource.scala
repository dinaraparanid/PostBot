package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.MessageEntity

import doobie.free.connection.ConnectionIO
import doobie.implicits.*

case object PostgresMessageEntityDataSource:
  def messageEntities: ConnectionIO[List[MessageEntity]] =
    sql"SELECT * FROM MessageEntity".query[MessageEntity].to[List]

  def getMessageEntitiesByPost(postId: Long): ConnectionIO[List[MessageEntity]] =
    sql"SELECT * FROM MessageEntity WHERE post_id = $postId".query[MessageEntity].to[List]

  def storeMessageEntity(
    entityType:    String,
    offset:        Int,
    length:        Int,
    url:           Option[String],
    customEmojiId: Option[String],
    postId:        Long,
  ): ConnectionIO[Long] =
    sql"""
    INSERT INTO MessageEntity(type, start_offset, length, url, custom_emoji_id, post_id)
    VALUES ($entityType, $offset, $length, ${url.orNull}, ${customEmojiId.orNull}, $postId)
    RETURNING id""".query[Long].unique

  def updateMessageEntity(
    id:               Long,
    newEntityType:    String,
    newOffset:        Int,
    newLength:        Int,
    newUrl:           Option[String],
    newCustomEmojiId: Option[String],
    newPostId:        Long,
  ): ConnectionIO[Int] =
    sql"""
    UPDATE MessageEntity SET
      type = $newEntityType,
      start_offset = $newOffset,
      length = $newLength,
      url = ${newUrl.orNull},
      custom_emoji_id = ${newCustomEmojiId.orNull},
      post_id = $newPostId
    WHERE id = $id""".update.run

  def deleteMessageEntity(id: Long): ConnectionIO[Int] =
    sql"DELETE FROM MessageEntity WHERE id = $id".update.run

  def deleteMessageEntitiesByPost(postId: Long): ConnectionIO[Int] =
    sql"DELETE FROM MessageEntity WHERE post_id = $postId".update.run

