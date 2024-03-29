package com.paranid5.tgpostbot
package data.post

import core.common.entities.post.{MessageEntity, Post, PostWithEntities}
import core.common.entities.user.User
import data.post.message_entity_src.PostgresMessageEntityDataSource
import data.post.message_entity_src.PostgresMessageEntityDataSource.given
import data.post.post_src.PostgresPostDataSource
import data.post.post_src.PostgresPostDataSource.given
import data.post.user_src.PostgresUserDataSource
import data.post.user_src.PostgresUserDataSource.given

import cats.effect.*
import cats.implicits.*

import doobie.*
import doobie.implicits.*

import io.github.cdimascio.dotenv.Dotenv

private val PostgresDbUrl      = "POSTGRES_DB_URL"
private val PostgresDbUser     = "POSTGRES_DB_USER"
private val PostgresDbPassword = "POSTGRES_DB_PASSWORD"

final class PostgresTgPostsRepository(
  private val transactor:     IOTransactor,
  private val messagesSource: PostgresMessageEntityDataSource,
  private val postSource:     PostgresPostDataSource,
  private val userSource:     PostgresUserDataSource
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

      PostgresTgPostsRepository(
        transactor     = transactor,
        messagesSource = PostgresMessageEntityDataSource(),
        postSource     = PostgresPostDataSource(),
        userSource     = PostgresUserDataSource()
      )

    extension (repository: PostgresTgPostsRepository)
      override def getPostsByUser(user: User): IO[List[PostWithEntities]] =
        def impl: ConnectionIO[List[PostWithEntities]] =
          for
            posts ← repository.postSource postsByUser user.id
            args  ← posts.map(postArgs(user)).sequence
          yield args map { case (p, u, es) ⇒ new PostWithEntities(p, u, es) }

        impl transact repository.transactor

      override def isPostExists(postId: Long): IO[Boolean] =
        repository.postSource
          .isPostExists(postId)
          .transact(repository.transactor)

      override def isPostBelongToUser(postId: Long, user: User): IO[Boolean] =
        repository.postSource
          .isPostBelongToUser(postId, user.id)
          .transact(repository.transactor)

      override def storePost(
        user:     User,
        date:     Int,
        text:     String,
        chatId:   Long,
        entities: List[MessageEntity]
      ): IO[Either[Throwable, Long]] =
        def tryStorePost(): ConnectionIO[Either[Throwable, Long]] =
          repository
            .postSource
            .storePost(user.id, date, text, chatId)
            .attempt

        def impl: ConnectionIO[Either[Throwable, Long]] =
          for
            _           ← repository.userSource.storeOrUpdateUser(user)
            postIdRes   ← tryStorePost()
            entitiesRes ← postIdRes
              .map(repository.messagesSource.storeEntities(_, entities))
              .sequence
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
        def tryUpdatePost(): ConnectionIO[Either[Throwable, Unit]] =
          repository
            .postSource
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
            updUserRes ← repository
              .userSource
              .updateUser(newUser)
              .attempt

            updPostRes ← updUserRes
              .map(_ ⇒ tryUpdatePost())
              .sequence

            res ← updPostRes
              .flatten
              .map(_ ⇒ repository.messagesSource.updateEntities(id, newEntities))
              .sequence
          yield res

        impl transact repository.transactor

      override def removePost(id: Long): IO[Either[Throwable, Long]] =
        repository
          .postSource
          .deletePost(id)
          .attempt
          .transact(repository.transactor)

      override def removePostByText(text: String): IO[Either[Throwable, Unit]] =
        repository
          .postSource
          .deletePostByText(text)
          .attempt
          .transact(repository.transactor)

      private def postArgs(user: User)(post: Post): ConnectionIO[(Post, User, List[MessageEntity])] =
        (post, user, repository.messagesSource entitiesByPost post.id).sequence