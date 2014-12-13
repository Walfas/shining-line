package com.herokuapp.shiningline.models

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification._
import play.api.libs.json._

class StickerSpec extends Specification {
  "Sticker" should {
    "be JSON serializable" in {
      val sticker: Sticker = Sticker(
        stickerVersion = 1,
        packageId = 3436,
        stickerId = 2487530,
        url = "http://example.com/image.png")

      val expected: JsValue = Json.obj(
        "stickerVersion" -> 1,
        "packageId" -> 3436,
        "stickerId" -> 2487530,
        "url" -> "http://example.com/image.png")

      Json.toJson(sticker) must_== expected
    }
  }
}

