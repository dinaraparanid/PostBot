package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.UserId.given
import core.common.entities.user.{User, UserId, UserState}
import data.user.UserCodec.given

import cats.effect.*
import cats.effect.std.Queue
import cats.implicits.*

import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import dev.profunktor.redis4cats.{Redis, RedisCommands}

import fs2.Stream

import io.circe.parser.decode
import io.circe.syntax.*

import scala.language.implicitConversions

final class RedisUserStateDataSource(
  private val requestQueue:  Queue[IO, Request],
  private val responseQueue: Queue[IO, UserState]
)

object RedisUserStateDataSource:
  given redisUserDataSource: UserStateDataSource[RedisUserStateDataSource] with
    override def launch: IO[RedisUserStateDataSource] =
      for
        requestQueue  ← Queue.unbounded[IO, Request]
        responseQueue ← Queue.unbounded[IO, UserState]
        _             ← run(requestQueue, responseQueue).start
      yield RedisUserStateDataSource(requestQueue, responseQueue)

    extension (s: RedisUserStateDataSource)
      override def userStateSource(user: User): IO[UserState] =
        for
          _     ← s.requestQueue offer Request.GetUserState(user)
          state ← s.responseQueue.take
        yield state

      override def patchUserState(userState: UserState): IO[Unit] =
        s.requestQueue offer Request.PatchUserState(userState)

private def run(
  requestQueue:  Queue[IO, Request],
  responseQueue: Queue[IO, UserState]
): IO[Unit] =
  def impl: Stream[IO, Unit] =
    for
      client ← Stream resource RedisClient[IO].from("redis://localhost")
      redis  ← Stream resource Redis[IO].fromClient(client, RedisCodec.Utf8)
      _      ← Stream eval processMessages(redis, requestQueue, responseQueue)
    yield ()

  impl.compile.drain

private def userKey(id: UserId): String =
  id.value.toString

private def processMessages(
  redis:         RedisCommands[IO, String, String],
  requestQueue:  Queue[IO, Request],
  responseQueue: Queue[IO, UserState]
): IO[Unit] =
  def impl: IO[Unit] =
    for
      req ← requestQueue.take
      _   ← handleRequest(redis, req, responseQueue)
    yield ()

  impl.foreverM

private def handleRequest(
  redis:         RedisCommands[IO, String, String],
  request:       Request,
  responseQueue: Queue[IO, UserState]
): IO[Unit] =
  request match
    case Request.GetUserState(user) ⇒
      sendUserState(redis, responseQueue, user)

    case Request.PatchUserState(userState) ⇒
      patchUserState(redis, userState)

private def sendUserState(
  redis:         RedisCommands[IO, String, String],
  responseQueue: Queue[IO, UserState],
  user:          User,
): IO[Unit] =
  def currentOrNone(maybeState: Option[String]): UserState =
    maybeState
      .flatMap(decode[UserState] >>> (_.toOption))
      .getOrElse(UserState.None(user))

  for
    maybeState ← redis get userKey(user.id)
    _          ← responseQueue offer currentOrNone(maybeState)
  yield ()

private def patchUserState(
  redis:     RedisCommands[IO, String, String],
  userState: UserState
): IO[Unit] =
  val key   = userKey(userState.user.id)
  val value = userState.asJson.noSpaces
  redis.set(key, value)
