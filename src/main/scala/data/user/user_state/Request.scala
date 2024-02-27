package com.paranid5.tgpostbot
package data.user.user_state

import core.common.entities.user.{User, UserState}

enum Request:
  case GetUserState(user: User)
  case PatchUserState(userState: UserState)
