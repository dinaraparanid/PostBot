package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.Post

import doobie.*
import doobie.implicits.*

object PostQueries:
  def posts: ConnectionIO[List[Post]] =
    sql"""SELECT * FROM "Post"""".query[Post].to[List]

  def postsByUser(userId: Long): ConnectionIO[List[Post]] =
    sql"""SELECT * FROM "Post" WHERE user_id = $userId""".query[Post].to[List]

  def storePost(
    userId: Long,
    date:   Int,
    text:   String,
    chatId: Long
  ): ConnectionIO[Long] =
    sql"""
    INSERT INTO "Post" (user_id, date, text, chat_id)
    VALUES ($userId, $date, $text, $chatId)
    RETURNING id""".query[Long].unique

  def updatePost(
    id:        Long,
    newUserId: Long,
    newDate:   Int,
    newText:   String,
    newChatId: Long
  ): ConnectionIO[Int] =
    sql"""
    UPDATE "Post" SET
      user_id = $newUserId,
      date = $newDate,
      text = $newText,
      chat_id = $newChatId
    WHERE id = $id""".update.run

  def deletePost(id: Long): ConnectionIO[Int] =
    sql"""DELETE FROM "Post" WHERE id = $id""".update.run