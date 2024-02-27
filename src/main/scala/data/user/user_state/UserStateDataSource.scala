package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.{UserId, UserState}

import cats.effect.IO
import cats.effect.std.Queue

trait UserStateDataSource[S]:
  def startStatesMonitoring: IO[S]

  def userStateSource(src: S, id: UserId): IO[UserState]

  def patchUserState(src: S, userState: UserState): IO[Unit]
