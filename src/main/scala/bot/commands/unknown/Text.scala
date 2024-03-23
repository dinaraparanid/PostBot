package com.paranid5.tgpostbot
package bot.commands.unknown

private def unknownText(command: String): String =
  s"Unknown command ${overflowCommand(command)}"

private def overflowCommand: String ⇒ String =
  case command if command.sizeIs <= 20 ⇒ command
  case command                         ⇒ s"${command take 20}..."
