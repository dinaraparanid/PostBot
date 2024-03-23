package com.paranid5.tgpostbot
package bot.commands.start

import core.common.entities.user.{UserState, User}
import data.user.user_state.UserStateDataSource

import cats.effect.IO

private def patchUserStartState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src patchUserState UserState.StartSent(user)
