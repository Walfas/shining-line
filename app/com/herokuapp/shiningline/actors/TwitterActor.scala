package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.services.TwitterService

import akka.actor.{Actor, Props}
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

object TwitterActor {
  case class TweetUploadRequest(id: Messages.Id, lineUrl: String)

  case class TweetSuccess(id: Messages.Id, tweetUrl: String)
  case class TweetFailure(id: Messages.Id, error: Throwable)

  def props(twitterService: TwitterService): Props =
    Props(new TwitterActor(twitterService))
}

class TwitterActor(twitterService: TwitterService) extends Actor {
  import TwitterActor._
  import context.dispatcher // ExecutionContext for Futures

  def receive = {
    case TweetUploadRequest(id, lineUrl) => {
      val originalSender = sender
      val f: Future[String] = twitterService.updateWithMediaFromUrl(lineUrl)

      f.onComplete {
        case Success(tweetUrl) => originalSender ! TweetSuccess(id, tweetUrl)
        case Failure(e) => originalSender ! TweetFailure(id, e)
      }
    }

    case _ => // Ignore unknown message
  }
}


