package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.{MessageEntity, Post, PostWithEntities}
import core.common.entities.user.User

import cats.effect.*
import cats.implicits.*

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
            posts ← PostQueries postsByUser user.id
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
          PostQueries
            .storePost(user.id, date, text, chatId)
            .attempt

        def impl: ConnectionIO[Either[Throwable, Long]] =
          for
            _           ← UserQueries storeOrUpdateUser user
            postIdRes   ← tryStorePost()
            entitiesRes ← postIdRes.map(MessageEntityQueries storeEntities entities).sequence
          yield entitiesRes flatMap (_ ⇒ postIdRes)

        impl transact repository.transactor

      override def updatePost(
        id:          Long,
        newUser:     User,
        newChatId:   Long,
        newDate:     Int,
        newText:     String,
        newEntities: List[MessageEntity]
      ): IO[Either[Throwable, Unit]] =
        def tryUpdatePost(): ConnectionIO[Either[Throwable, Int]] =
          PostQueries
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
            updUserRes ← UserQueries
              .updateUser(newUser)
              .attempt

            updPostRes ← updUserRes
              .map(_ ⇒ tryUpdatePost())
              .sequence

            res ← updPostRes
              .flatten
              .map(_ ⇒ MessageEntityQueries.updateEntities(newEntities, id))
              .sequence
          yield res

        impl transact repository.transactor

      override def removePost(id: Long): IO[Either[Throwable, Unit]] =
        PostQueries
          .deletePost(id)
          .map(_ ⇒ ())
          .attempt
          .transact(repository.transactor)

private def postArgs(user: User)(post: Post): ConnectionIO[(Post, User, List[MessageEntity])] =
  (post, user, MessageEntityQueries entitiesByPost post.id).sequence