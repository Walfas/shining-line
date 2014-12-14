package com.herokuapp.shiningline.services

import com.herokuapp.shiningline.models._

import org.specs2.mutable._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._
import play.api.test.Helpers._

class StickersServiceSpec extends Specification {
  "SlickStickersService" >> {
    val fakeApp: FakeApplication =
      FakeApplication(additionalConfiguration = inMemoryDatabase())

    "saveSticker and findSticker" should {
      "return the sticker" in new WithApplication(fakeApp) {
        val dao = new DAO(DB.driver)
        val stickersService = new SlickStickersService(DB, dao)

        val sticker = Sticker(1, 2, 3, "hello")
        stickersService.saveSticker(sticker)

        stickersService.findSticker(1, 2, 3) must_== Some(sticker)
      }
    }
  }
}

