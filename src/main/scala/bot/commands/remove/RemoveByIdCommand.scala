package com.paranid5.tgpostbot
package bot.commands.remove

import data.post.TgPostsRepository
import data.user.user_state.UserStateDataSource
import utils.telegram.{botUser, chatId}

import cats.effect.IO

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

def onRemoveByIdCommand[U: UserStateDataSource, R: TgPostsRepository](
  bot:             TelegramBot,
  message:         Message,
  stateSource:     U,
  postsRepository: R
): IO[SendResponse] =
  def sendNotNumber: SendResponse =
    bot execute SendMessage(message.chatId, notNumberText)

  message.text
    .toLongOption
    .fold(ifEmpty = IO(sendNotNumber)):
      onPostIdParsed(_, bot, message, stateSource, postsRepository)

private def onPostIdParsed[U: UserStateDataSource, R: TgPostsRepository](
  postId:          Long,
  bot:             TelegramBot,
  message:         Message,
  stateSource:     U,
  postsRepository: R
): IO[SendResponse] =
  val user = message.botUser

  def handleExists(exists: Boolean): IO[SendResponse] =
    if exists then handleBelong else IO(sendNotExists)

  def handleBelong: IO[SendResponse] =
    for
      belong ← postsRepository.isPostBelongToUser(postId, user)
      result ← if belong then handlePostRemove else IO(sendNotBelong)
    yield result

  def handlePostRemove: IO[SendResponse] =
    for
      result   ← postsRepository removePost postId
      response ← result.fold(
        e ⇒ IO(sendSomethingWrong(Option(e.getMessage))),
        _ ⇒ patchUserRemoveIdSentState(user, stateSource) map (_ ⇒ sendSuccessfullyRemoved)
      )
    yield response

  def sendNotExists: SendResponse =
    bot execute SendMessage(message.chatId, notFoundText(postId))

  def sendNotBelong: SendResponse =
    bot execute SendMessage(message.chatId, notBelongText(postId))

  def sendSomethingWrong(reason: Option[String]): SendResponse =
    bot execute SendMessage(message.chatId, somethingWentWrongText(reason))

  def sendSuccessfullyRemoved: SendResponse =
    bot execute SendMessage(message.chatId, successfullyRemovedText(postId))

  for
    exists ← postsRepository isPostExists postId
    res    ← handleExists(exists)
  yield res