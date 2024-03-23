package com.paranid5.tgpostbot
package data.post.post_src

import core.common.entities.post.Post

import doobie.ConnectionIO
import doobie.implicits.*

final class PostgresPostDataSource

object PostgresPostDataSource:
  given PostDataSource[ConnectionIO, PostgresPostDataSource] with
    extension (source: PostgresPostDataSource)
      override def posts: ConnectionIO[List[Post]] =
        sql"""SELECT * FROM "Post"""".query[Post].to[List]

      override def postsByUser(userId: Long): ConnectionIO[List[Post]] =
        sql"""SELECT * FROM "Post" WHERE user_id = $userId""".query[Post].to[List]

      override def isPostExists(postId: Long): ConnectionIO[Boolean] =
        sql""" SELECT count(id) > 0 FROM "Post" WHERE id = $postId"""
          .query[Boolean].unique

      override def isPostBelongToUser(postId: Long, userId: Long): ConnectionIO[Boolean] =
        sql"""SELECT count(id) > 0 FROM "Post" WHERE id = $postId AND user_id = $userId"""
          .query[Boolean].unique

      override def storePost(
        userId: Long,
        date:   Int,
        text:   String,
        chatId: Long
      ): ConnectionIO[Long] =
        sql"""
        INSERT INTO "Post" (user_id, date, text, chat_id)
        VALUES ($userId, $date, $text, $chatId)
        RETURNING id""".query[Long].unique

      override def updatePost(
        id:        Long,
        newUserId: Long,
        newDate:   Int,
        newText:   String,
        newChatId: Long
      ): ConnectionIO[Unit] =
        sql"""
        UPDATE "Post" SET
          user_id = $newUserId,
          date = $newDate,
          text = $newText,
          chat_id = $newChatId
        WHERE id = $id""".update.run.map(_ ⇒ ())

      override def deletePost(id: Long): ConnectionIO[Long] =
        sql"""DELETE FROM "Post" WHERE id = $id""".update.run.map(_ ⇒ id)

      override def deletePostByText(text: String): ConnectionIO[Unit] =
        sql"""DELETE FROM "Post" WHERE text = $text""".update.run.map(_ ⇒ ())

