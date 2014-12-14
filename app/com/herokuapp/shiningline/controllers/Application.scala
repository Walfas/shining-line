package com.herokuapp.shiningline.controllers

import com.herokuapp.shiningline.ShiningLine
import com.herokuapp.shiningline.services._
import com.herokuapp.shiningline.models._

import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.{Future, Promise}
import scala.util.{Try, Success, Failure}
import twitter4j.Twitter

object Application
    extends ApplicationController
    with TwitterServiceComponent
    with StickersServiceComponent {
  lazy val twitterService: TwitterService = ShiningLine.twitterService
  lazy val stickersService: StickersService = ShiningLine.stickersService
}

trait ApplicationController extends Controller {
  this: TwitterServiceComponent with StickersServiceComponent =>

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def getSticker(stickerVersion: Int, packageId: Int, stickerId: Int) = Action.async {
    val p: Promise[Sticker] = Promise[Sticker]

    val stickerO: Option[Sticker] =
      stickersService.find(stickerVersion, packageId, stickerId)

    if (stickerO.nonEmpty) {
      p.success(stickerO.get)
    } else {
      val url: String = stickersService.getUrl(stickerVersion, packageId, stickerId)
      val f: Future[String] = twitterService.updateWithMediaFromUrl(url)
      f.onComplete {
        case Success(url) => {
          val sticker: Sticker = Sticker(stickerVersion, packageId, stickerId, url)
          stickersService.save(sticker)
          p success sticker
        }
        case Failure(e) =>
          p failure e
      }
    }

    p.future.map {
      (sticker: Sticker) => Ok(Json.toJson(sticker))
    } recover {
      case (e: Throwable) => InternalServerError(e.toString)
    }
  }
}

