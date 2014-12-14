package com.herokuapp.shiningline.models

import org.specs2.mutable._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._

class DAOSpec extends Specification {
  "DAO" should {
    "work as expected" in new WithApplication {
      val dao = new DAO(DB.driver)

      DB.withSession { implicit s: Session =>
        val stickers: Seq[Sticker] = Seq(
          Sticker(1, 2, 3, "hello"),
          Sticker(4, 5, 6, "world"),
          Sticker(7, 8, 9, "!"))

        dao.stickers.insertAll(stickers:_*)
        dao.stickers.list must_== stickers
      }
    }
  }
}

