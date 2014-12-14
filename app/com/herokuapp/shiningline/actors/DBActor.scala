package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker
import com.herokuapp.shiningline.services.StickersService

import akka.actor.{Actor, Props}
import scala.util.{Try, Success, Failure}

object DBActor {
  case class StickerFindRequest(id: Messages.Id)
  case class StickerSaveRequest(sticker: Sticker)

  case class StickerFound(id: Messages.Id, sticker: Sticker)
  case class StickerNotFound(id: Messages.Id, url: String)
  case class StickerFailure(id: Messages.Id, error: Throwable)

  def props(stickersService: StickersService): Props =
    Props(new DBActor(stickersService))
}

class DBActor(stickersService: StickersService) extends Actor {
  import DBActor._

  def receive: Receive = {
    case StickerFindRequest(id) => {
      val result: Try[Option[Sticker]] =
        Try(stickersService.find(id.stickerVersion, id.packageId, id.stickerId))

      result match {
        case Success(Some(sticker)) => sender ! StickerFound(id, sticker)
        case Success(None) => {
          val url = stickersService.getUrl(id.stickerVersion, id.packageId, id.stickerId)
          sender ! StickerNotFound(id, url)
        }
        case Failure(e) => sender ! StickerFailure(id, e) // DB failed
      }
    }

    case StickerSaveRequest(sticker) => {
      stickersService.save(sticker)
    }

    case msg => // Ignore unknown message
  }
}

