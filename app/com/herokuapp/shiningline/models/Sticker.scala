package com.herokuapp.shiningline.models

import play.api.libs.json.{Json, Format}
import play.api.db.slick.Profile

case class Sticker(
    stickerVersion: Int = 0,
    packageId: Int = 0,
    stickerId: Int = 0,
    url: String = "")

object Sticker {
  implicit val stickerFormat: Format[Sticker] = Json.format[Sticker]
}

trait StickerComponent {
  this: Profile =>

  import profile.simple._

  val stickers = TableQuery[StickersTable]

  class StickersTable(tag: Tag) extends Table[Sticker](tag, "stickers") {
    def stickerVersion = column[Int]("sticker_version")
    def packageId = column[Int]("package_id")
    def stickerId = column[Int]("sticker_id")
    def url = column[String]("url")

    def * = (stickerVersion, packageId, stickerId, url) <> ((Sticker.apply _).tupled, Sticker.unapply _)

    def pk = primaryKey("pk_stickers", (stickerVersion, packageId, stickerId))
  }
}

