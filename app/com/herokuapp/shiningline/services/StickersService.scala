package com.herokuapp.shiningline.services

import com.herokuapp.shiningline.models.{DAO, Sticker}

import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend.Database

trait StickersService {
  def getUrl(stickerVersion: Int, packageId: Int, stickerId: Int): String
  def find(stickerVersion: Int, packageId: Int, stickerId: Int): Option[Sticker]
  def save(sticker: Sticker): Unit
}

trait StickersServiceComponent {
  def stickersService: StickersService
}

class SlickStickersService(
    db: Database,
    dao: DAO,
    lineUrl: String = "") extends StickersService {
  def getUrl(
      stickerVersion: Int,
      packageId: Int,
      stickerId: Int): String = {

    val verPath: String = (stickerVersion/100000).toInt + "/" +
      (stickerVersion / 1000).toInt + "/" +
      (stickerVersion % 1000)

    s"$lineUrl/$verPath/$packageId/android/stickers/$stickerId.png"
  }

  def save(sticker: Sticker): Unit = {
    val existing: Option[Sticker] = find(
      sticker.stickerVersion,
      sticker.packageId,
      sticker.stickerId)

    if (existing.nonEmpty && existing.get == sticker) {
      return // Sticker already exists, so do nothing
    }

    db.withSession { implicit session =>
      if (existing.isEmpty)
        dao.stickers.insert(sticker).run
      else
        dao.stickers.update(sticker).run
    }
  }

  def find(
      stickerVersion: Int,
      packageId: Int,
      stickerId: Int): Option[Sticker] = {
    db.withSession { implicit session =>
      dao.stickers.filter { stk =>
        stk.stickerVersion === stickerVersion &&
        stk.packageId === packageId &&
        stk.stickerId === stickerId
      }.firstOption
    }
  }
}

