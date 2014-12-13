package com.herokuapp.shiningline.models

import play.api.libs.json.{Json, Format}

case class Sticker(
    stickerVersion: Int,
    packageId: Int,
    stickerId: Int,
    url: String)

object Sticker {
  implicit val stickerFormat: Format[Sticker] = Json.format[Sticker]
}

