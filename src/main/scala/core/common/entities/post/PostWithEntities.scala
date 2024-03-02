package com.paranid5.tgpostbot
package core.common.entities.post

import core.common.entities.user.User

final case class PostWithEntities(
  id:       Long,
  user:     User,
  date:     Int,
  text:     String,
  entities: List[MessageEntity]
):
  def this(post: Post, user: User, entities: List[MessageEntity]) =
    this(
      id       = post.id,
      user     = user,
      date     = post.date,
      text     = post.text,
      entities = entities
    )
