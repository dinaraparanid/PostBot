package com.paranid5.tgpostbot
package bot.commands.store

private def requestPostText: String =
  "Provide a post text. You can either enter it yourself or forward the post to me"

private def postReceivedText(id: Long): String =
  s"Post #$id is successfully stored"
