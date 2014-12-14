package com.herokuapp.shiningline.controllers

import com.herokuapp.shiningline.ShiningLine
import com.herokuapp.shiningline.actors._
import com.herokuapp.shiningline.services._
import com.herokuapp.shiningline.models._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}
import twitter4j.Twitter

object Application
    extends ApplicationControllerActors
    with ManagerActorComponent {
  lazy val managerActor: ActorRef = ShiningLine.managerActor
  lazy val twitterService: TwitterService = ShiningLine.twitterService
  lazy val stickersService: StickersService = ShiningLine.stickersService
  lazy implicit val timeout: Timeout = Duration(ShiningLine.timeout, MILLISECONDS)
}

trait ApplicationController extends Controller {
  def getSticker(stickerVersion: Int, packageId: Int, stickerId: Int): Action[AnyContent]
}

trait ApplicationControllerActors extends ApplicationController {
  this: ManagerActorComponent =>

  implicit def timeout: Timeout

  implicit val context: ExecutionContext =
    play.api.libs.concurrent.Execution.Implicits.defaultContext

  def getSticker(stickerVersion: Int, packageId: Int, stickerId: Int) = Action.async {
    val msg = ManagerActor.StickerRequest(Messages.Id(stickerVersion, packageId, stickerId))
    val f: Future[Any] = managerActor ? msg

    f.map {
      case ManagerActor.StickerSuccess(sticker) => {
        Ok(Json.toJson(sticker))
      }
      case ManagerActor.StickerFailure(e) => {
        val error = Error("500", e.toString)
        InternalServerError(Json.toJson(error))
      }
    } recover {
      case e: Throwable => {
        val error = Error("500", e.toString)
        InternalServerError(Json.toJson(error))
      }
    }
  }
}

trait ApplicationControllerFutures extends ApplicationController {
  this: TwitterServiceComponent with StickersServiceComponent =>

  implicit val context: ExecutionContext =
    play.api.libs.concurrent.Execution.Implicits.defaultContext

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

