package com.paranid5.tgpostbot
package bot.commands.start

def startCommandText: String =
  """Hello! I am Post Bot - bot that helps to store and order telegram post.
    |I can do the following:
    |
    |***/set_db - Sets credentials of your firebase DB. All post will be stored there***
    |***/store*** - Stores post in database. Later you can access them in your server or client app
    |***/remove*** - Removes post from the database.
    |***/update*** - Refreshes post content in your database
    |***/help - Gives detailed description of every command***""".stripMargin
