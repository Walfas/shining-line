package com.herokuapp.shiningline.services

import com.herokuapp.shiningline.models._

import org.specs2.mock._
import org.specs2.mutable._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._
import scala.slick.jdbc.JdbcBackend.Database

class StickersServiceSpec extends Specification with Mockito {
  "SlickStickersService" >> {

    "getUrl" should {
      val baseUrl = "http://example.com"
      val stickersService = new SlickStickersService(
        mock[Database], mock[DAO], baseUrl)

      "return the correct URL" in {
        val result: String = stickersService.getUrl(1, 2, 3)
        result must_== "http://example.com/0/0/1/2/android/stickers/3.png"
      }
    }

    def fakeApp(): FakeApplication =
      FakeApplication(additionalConfiguration = inMemoryDatabase())

    "save and find " should {
      "return the saved sticker" in new WithApplication(fakeApp) {
        val dao = new DAO(DB.driver)
        val stickersService = new SlickStickersService(DB, dao)

        val sticker = Sticker(1, 2, 3, "hello")
        stickersService.save(sticker)

        stickersService.find(1, 2, 3) must beSome(sticker)
      }
    }

    "save" should {
      "overwrite existing stickers" in new WithApplication(fakeApp) {
        val dao = new DAO(DB.driver)
        val stickersService = new SlickStickersService(DB, dao)

        val sticker1 = Sticker(7, 6, 5, "hello")
        stickersService.save(sticker1)

        val sticker2 = Sticker(7, 6, 5, "world")
        stickersService.save(sticker2)

        stickersService.find(7, 6, 5) must beSome(sticker2)
      }
    }

    "find" should {
      "return None if no sticker found" in new WithApplication(fakeApp) {
        val dao = new DAO(DB.driver)
        val stickersService = new SlickStickersService(DB, dao)

        stickersService.find(25, 25, 2) must beNone
      }
    }
  }
}

