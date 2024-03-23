package com.paranid5.tgpostbot
package bot.commands.remove

import core.common.entities.user.{User, UserState}
import data.user.user_state.UserStateDataSource

import cats.effect.IO

private def patchUserRemoveSentState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src patchUserState UserState.RemoveSent(user)

private def patchUserRemoveIdSentState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src patchUserState UserState.RemoveIdSent(user)