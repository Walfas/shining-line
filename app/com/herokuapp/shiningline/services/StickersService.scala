package com.herokuapp.shiningline.services

import com.herokuapp.shiningline.models.{DAO, Sticker}

import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend.Database

trait StickersService {
  def findSticker(stickerVersion: Int, packageId: Int, stickerId: Int): Option[Sticker]
  def saveSticker(sticker: Sticker): Unit
}

trait StickersServiceComponent {
  def stickersService: StickersService
}

class SlickStickersService(db: Database, dao: DAO) extends StickersService {
  def saveSticker(sticker: Sticker): Unit = {
    db.withSession { implicit session =>
      dao.stickers.insertOrUpdate(sticker).run
    }
  }

  def findSticker(
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

