package com.paranid5.tgpostbot
package data.post.post_src

import core.common.entities.post.Post

trait PostDataSource[F[_], A]:
  extension (source: A)
    def posts: F[List[Post]]

    def postsByUser(userId: Long): F[List[Post]]

    def storePost(
      userId: Long,
      date:   Int,
      text:   String,
      chatId: Long
    ): F[Long]

    def updatePost(
      id:        Long,
      newUserId: Long,
      newDate:   Int,
      newText:   String,
      newChatId: Long
    ): F[Unit]

    def deletePost(id: Long): F[Unit]

    def deletePostByText(text: String): F[Unit]
