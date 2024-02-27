package com.paranid5.tgpostbot
package bot.commands.store

import core.common.entities.user.{UserState, User}
import data.user.user_state.UserStateDataSource

import cats.effect.IO

def patchUserStoreSentState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src.patchUserState(UserState.StoreSent(user))

def patchUserStorePostSentState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src.patchUserState(UserState.StorePostSent(user))
