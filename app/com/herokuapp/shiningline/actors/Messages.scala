package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker

object Messages {
  case class Id(
      stickerVersion: Int,
      packageId: Int,
      stickerId: Int)
}

