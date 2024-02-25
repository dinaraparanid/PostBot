package com.paranid5.tgpostbot.data.user

import com.paranid5.tgpostbot.core.common.entities.user.{User, UserId, UserState}

import monix.eval.Task
import monix.reactive.Observable

trait UserDataSource:
  def userSource: Observable[List[User]]

  def patchUser(user: User): Task[Unit]

  def userStatesSource: Observable[Map[UserId, UserState]]

  def patchUserState(userState: UserState): Task[Unit]
