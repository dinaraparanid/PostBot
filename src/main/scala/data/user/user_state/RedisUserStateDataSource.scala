package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.UserId.given
import core.common.entities.user.{UserId, UserState}
import data.user.UserCodec.given

import cats.effect.*
import cats.effect.std.Queue
import cats.implicits.*

import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.{RedisChannel, RedisCodec}
import dev.profunktor.redis4cats.effect.Log.Stdout.given
import dev.profunktor.redis4cats.pubsub.{PubSub, PubSubCommands}

import fs2.{Pipe, Stream}

import io.circe.parser.decode
import io.circe.syntax.*

import scala.language.implicitConversions

final class RedisUserStateDataSource(
  private val requestQueue:  Queue[IO, Request],
  private val responseQueue: Queue[IO, UserState]
)

object RedisUserStateDataSource:
  given redisDataSource: UserStateDataSource[RedisUserStateDataSource] with
    override def startStatesMonitoring: IO[RedisUserStateDataSource] =
      for
        requestQueue  ← Queue.unbounded[IO, Request]
        responseQueue ← Queue.unbounded[IO, UserState]
        _             ← run(requestQueue, responseQueue).start
      yield RedisUserStateDataSource(requestQueue, responseQueue)

    override def userStateSource(
      src: RedisUserStateDataSource,
      id:  UserId
    ): IO[UserState] =
      for
        _ ← src.requestQueue offer GetUserState(id)
        state ← src.responseQueue.take
      yield state

    override def patchUserState(
      src:       RedisUserStateDataSource,
      userState: UserState
    ): IO[Unit] =
      src.requestQueue offer PatchUserState(userState)

private def run(
  requestQueue:  Queue[IO, Request],
  responseQueue: Queue[IO, UserState]
): IO[Unit] =
  def impl: Stream[IO, Unit] =
    for
      client ← Stream resource RedisClient[IO].from("redis://localhost")
      pubSub ← Stream resource PubSub.mkPubSubConnection[IO, String, String](client, RedisCodec.Utf8)
      _      ← Stream eval processMessages(pubSub, requestQueue, responseQueue)
    yield ()

  impl.compile.drain

private def userChannel(id: UserId): RedisChannel[String] =
  RedisChannel(id.value.toString)

private def processMessages(
  pubSub:        PubSubCommands[[A] =>> Stream[IO, A], String, String],
  requestQueue:  Queue[IO, Request],
  responseQueue: Queue[IO, UserState]
): IO[Unit] =
  def impl: IO[Stream[IO, Unit]] =
    for req ← requestQueue.take
      yield handleRequest(pubSub, req, responseQueue)

  impl.foreverM

private def handleRequest(
  pubSub:        PubSubCommands[[A] =>> Stream[IO, A], String, String],
  request:       Request,
  responseQueue: Queue[IO, UserState]
): Stream[IO, Unit] =
  request match
    case GetUserState(userId) ⇒
      val sub = pubSub subscribe userChannel(userId)
      sendUserState(sub, responseQueue)

    case PatchUserState(userState) ⇒
      val id: UserId = userState.user.id
      val chan = userChannel(id)
      val pub = pubSub publish chan
      patchUserState(pub, userState)

private def sendUserState(
  sub:           Stream[IO, String],
  responseQueue: Queue[IO, UserState]
): Stream[IO, Unit] =
  def impl: Pipe[IO, String, Unit] =
    _ evalMap
      decode[UserState] >>>
        (_.fold(fa = _ ⇒ IO pure (), fb = responseQueue.offer))

  sub through impl

private def patchUserState(
  pub:       Pipe[IO, String, Unit],
  userState: UserState
): Stream[IO, Unit] =
  def impl: Stream[IO, String] =
    Stream eval (IO pure userState.asJson.noSpaces)

  impl through pub
