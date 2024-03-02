package com.paranid5.tgpostbot
package data.post

import cats.effect.IO

import com.pengrad.telegrambot.model.Message

import doobie.*
import doobie.implicits.*
import doobie.util.transactor
import doobie.util.transactor.Transactor

final class PostgresPostDataSource(
  private val transactor: IOTransactor
)

object PostgresPostDataSource:
  given postgresPostDataSource: PostDataSource[PostgresPostDataSource] with
    extension (source: PostgresPostDataSource)
      override def storePost(
        userId:     Long,
        date:       Int,
        text:       String,
      ): IO[Long] =
        val query = sql"""
                         INSERT INTO Post(user_id, date, text)
                         VALUES ($userId, $date, $text)
                         RETURNING ID;"""
          .query[Long]

        query.unique transact source.transactor

      override def updatePost(
        id:         Long,
        newUserId:  Long,
        newDate:    Int,
        newText:    String,
      ): IO[Unit] =
        val query = sql"""
                         UPDATE Post SET user_id = $newUserId, date = $newDate, text = $newText
                         WHERE id = $id"""
          .update

        (query.run transact source.transactor).void

      override def deletePost(id: Long): IO[Unit] =
        val query = sql"DELETE FROM Post WHERE id = $id".update
        (query.run transact source.transactor).void