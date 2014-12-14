package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker

object Messages {
  case class Id(
      stickerVersion: Int = 0,
      packageId: Int = 0,
      stickerId: Int = 0)
}

