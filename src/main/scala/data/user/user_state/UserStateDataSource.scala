package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.{UserId, UserState}

import monix.eval.Task
import monix.reactive.Observable

trait UserStateDataSource[S]:
  def userStatesSource: Observable[Map[UserId, UserState]]

  def patchUserState(userState: UserState): Task[Unit]
