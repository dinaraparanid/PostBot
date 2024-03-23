package com.paranid5.tgpostbot
package bot.commands.help

private def helpCommandText: String =
  """start - Describes main functionality and purpose of the bot
    |set_db - Sets credentials of user's firebase DB
    |store - Stores post in the user's database
    |remove - Removes post from the user's database
    |update - Updates post content in the user's database
    |help - Describes every command in details""".stripMargin
