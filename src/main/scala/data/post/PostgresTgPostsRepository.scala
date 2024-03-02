package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.{MessageEntity, Post, PostWithEntities}
import core.common.entities.user.User
import data.user.PostgresUserDataSource

import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*

import doobie.*
import doobie.implicits.*

import io.github.cdimascio.dotenv.Dotenv

private val PostgresDbUrl      = "POSTGRES_DB_URL"
private val PostgresDbUser     = "POSTGRES_DB_USER"
private val PostgresDbPassword = "POSTGRES_DB_PASSWORD"

final class PostgresTgPostsRepository(
  private val transactor: IOTransactor
)

object PostgresTgPostsRepository:
  given postgresTgPostRepository: TgPostsRepository[PostgresTgPostsRepository] with
    override def connect(dotenv: Dotenv): PostgresTgPostsRepository =
      val transactor = Transactor.fromDriverManager[IO](
        driver       = "org.postgresql.Driver",
        url          = dotenv get PostgresDbUrl,
        user         = dotenv get PostgresDbUser,
        password     = dotenv get PostgresDbPassword,
        logHandler   = None
      )

      PostgresTgPostsRepository(transactor)

    extension (repository: PostgresTgPostsRepository)
      override def getPostsByUser(user: User): IO[List[PostWithEntities]] =
        def impl: ConnectionIO[List[PostWithEntities]] =
          for
            posts ← PostgresPostDataSource getPostsByUser user.id
            args  ← posts.map(postArgs(user)).sequence
          yield args map { case (p, u, es) ⇒ new PostWithEntities(p, u, es) }

        impl transact repository.transactor

      override def storePost(
        user:     User,
        date:     Int,
        text:     String,
        chatId:   Long,
        entities: List[MessageEntity]
      ): IO[Either[Throwable, Long]] =
        def tryStorePost(): ConnectionIO[Either[Throwable, Long]] =
          PostgresPostDataSource
            .storePost(user.id, date, text, chatId)
            .attempt

        def impl: ConnectionIO[Either[Throwable, Long]] =
          for
            _           ← PostgresUserDataSource storeOrUpdateUser user
            postIdRes   ← tryStorePost()
            entitiesRes ← postIdRes.map(addEntities(entities)).sequence
          yield entitiesRes map (_.head)

        impl transact repository.transactor

      def updatePost(
        id:          Long,
        newUser:     User,
        newChatId:   Long,
        newDate:     Int,
        newText:     String,
        newEntities: List[MessageEntity]
      ): IO[Either[Throwable, Unit]] =
        def tryUpdatePost(): ConnectionIO[Either[Throwable, Int]] =
          PostgresPostDataSource
            .updatePost(
              id        = id,
              newUserId = newUser.id,
              newDate   = newDate,
              newText   = newText,
              newChatId = newChatId
            )
            .attempt

        def impl: ConnectionIO[Either[Throwable, Unit]] =
          for
            updUserRes ← PostgresUserDataSource
              .updateUser(newUser)
              .attempt

            updPostRes ← updUserRes
              .map(_ ⇒ tryUpdatePost())
              .sequence

            res ← updPostRes
              .flatten
              .map(_ ⇒ updateEntities(newEntities, id))
              .sequence
          yield res

        impl transact repository.transactor

      def removePost(id: Long): IO[Either[Throwable, Unit]] =
        PostgresPostDataSource
          .deletePost(id)
          .map(_ ⇒ ())
          .attempt
          .transact(repository.transactor)

private def postArgs(user: User)(post: Post): ConnectionIO[(Post, User, List[MessageEntity])] =
  (post, user, PostgresMessageEntityDataSource getMessageEntitiesByPost post.id).sequence

private def addEntities(entities: List[MessageEntity])(postId: Long): ConnectionIO[List[Long]] =
  entities
    .map: entity ⇒
      PostgresMessageEntityDataSource.storeMessageEntity(
        entityType    = entity.entityType,
        offset        = entity.offset,
        length        = entity.length,
        url           = entity.url,
        customEmojiId = entity.customEmojiId,
        postId        = postId
      ).map(_ ⇒ postId)
    .sequence

private def updateEntities(
  entities: List[MessageEntity],
  postId:   Long
): ConnectionIO[Unit] =
  for
    _ ← PostgresMessageEntityDataSource deleteMessageEntitiesByPost postId
    _ ← addEntities(entities)(postId)
  yield ()