package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker

import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.{Future, Promise}
import scala.util.{Try, Success, Failure}

object ManagerActor {
  case class StickerRequest(id: Messages.Id)

  trait StickerResponse
  case class StickerSuccess(sticker: Sticker) extends StickerResponse
  case class StickerFailure(error: Throwable) extends StickerResponse

  case class RemoveFromPending(id: Messages.Id)

  def props(dbActor: ActorRef, twitterActor: ActorRef): Props =
    Props(new ManagerActor(dbActor, twitterActor))
}

class ManagerActor(dbActor: ActorRef, twitterActor: ActorRef) extends Actor {
  import ManagerActor._
  import context.dispatcher // ExecutionContext for Futures

  var pending: Map[Messages.Id, Promise[StickerResponse]] = Map.empty

  def receive: Receive = {
    case StickerRequest(id) => {
      val f: Future[StickerResponse] = pending.getOrElse(id, newRequest(id)).future
      val originalSender: ActorRef = sender

      f.onComplete {
        case Success(response) => originalSender ! response
        case Failure(e) => originalSender ! StickerFailure(e)
      }
    }

    case RemoveFromPending(id) => pending -= id

    case DBActor.StickerFound(id, sticker) => {
      val response = StickerSuccess(sticker)
      completeRequest(id, response)
    }

    case DBActor.StickerNotFound(id, url) => {
      twitterActor ! TwitterActor.TweetUploadRequest(id, url)
    }

    case DBActor.StickerFailure(id, e) => {
      val response = StickerFailure(e)
      completeRequest(id, response)
    }

    case TwitterActor.TweetSuccess(id, tweetUrl) => {
      val sticker = Sticker(
        id.stickerVersion,
        id.packageId,
        id.stickerId,
        tweetUrl)

      dbActor ! DBActor.StickerSaveRequest(sticker)

      val response = StickerSuccess(sticker)

      completeRequest(id, response)
    }

    case _ => // Ignore
  }

  def newRequest(id: Messages.Id): Promise[StickerResponse] = {
    dbActor ! DBActor.StickerFindRequest(id)

    val promise: Promise[StickerResponse] = Promise[StickerResponse]
    pending += (id -> promise)

    promise
  }

  def completeRequest(id: Messages.Id, response: StickerResponse): StickerResponse = {
    if (pending.isDefinedAt(id)) {
      pending(id).success(response)
      self ! RemoveFromPending(id)
    }

    response
  }
}

