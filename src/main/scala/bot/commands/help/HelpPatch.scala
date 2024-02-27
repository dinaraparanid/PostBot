package com.paranid5.tgpostbot
package bot.commands.help

import core.common.entities.user.{UserState, User}
import data.user.user_state.UserStateDataSource

import cats.effect.IO

def patchUserHelpSentState[S: UserStateDataSource](user: User, src: S): IO[Unit] =
  src.patchUserState(UserState.HelpSent(user))
