package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.{User, UserState}

import cats.effect.IO

trait UserStateDataSource[S]:
  def launch: IO[S]

  extension(s: S)
    def userStateSource(user: User): IO[UserState]
    def patchUserState(userState: UserState): IO[Unit]
