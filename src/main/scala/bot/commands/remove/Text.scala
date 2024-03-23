package com.paranid5.tgpostbot
package bot.commands.remove

private def removeCommandText: String =
  "Enter the ID of the post that is going to be deleted:"

private def notBelongText(id: Long): String =
  s"The post $id does not belong to you. Try again"

private def notFoundText(id: Long): String =
  s"Post $id was not found. Try again"

private def notNumberText: String =
  "Not a number. Please, enter the ID of the post:"

private def somethingWentWrongText(reason: Option[String]): String =
  s"Something went wrong${reason.fold("")(x ⇒ s": $x")}. Try again"

private def successfullyRemovedText(id: Long): String =
  s"Post $id is successfully removed!"
