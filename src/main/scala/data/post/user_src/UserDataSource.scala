package com.paranid5.tgpostbot
package data.post.user_src

import core.common.entities.user.User

trait UserDataSource[F[_], S]:
  extension (source: S)
    def users: F[List[User]]

    def userById(id: Long): F[Option[User]]

    def storeOrUpdateUser_(
      id:        Long,
      chatId:    Long,
      userName:  String,
      firstName: Option[String],
      lastName:  Option[String]
    ): F[Int]

    def storeOrUpdateUser(user: User): F[Int] =
      source.storeOrUpdateUser_(
        id        = user.id,
        chatId    = user.chatId,
        userName  = user.userName,
        firstName = user.firstName,
        lastName  = user.lastName
      )

    def updateUser_(
      id:           Long,
      newChatId:    Long,
      newUserName:  String,
      newFirstName: Option[String],
      newLastName:  Option[String]
    ): F[Unit]

    def updateUser(user: User): F[Unit] =
      source.updateUser_(
        id           = user.id,
        newChatId    = user.chatId,
        newUserName  = user.userName,
        newFirstName = user.firstName,
        newLastName  = user.lastName
      )

    def deleteUser(id: Long): F[Unit]