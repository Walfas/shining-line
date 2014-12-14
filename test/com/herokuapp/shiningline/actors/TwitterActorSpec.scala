package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.services.TwitterService

import akka.testkit.TestActorRef
import org.specs2.mock._
import org.specs2.mutable._
import scala.concurrent.Future

class TwitterActorSpec extends Specification with Mockito with ActorSpecHelpers {
  "TwitterActor" should {
    import TwitterActor._

    "send TweetSuccess on successful" in new ActorSpec {
      val mockTS = mock[TwitterService]
      mockTS.updateWithMediaFromUrl(any[String]) returns Future.successful("output")

      val actor = TestActorRef(TwitterActor.props(mockTS))
      val id = Messages.Id(1, 2, 3)
      actor ! TweetUploadRequest(id, "input")

      there was one(mockTS).updateWithMediaFromUrl("input")

      expectMsg(TweetSuccess(id, "output"))
    }

    "send TweetFailure on failure" in new ActorSpec {
      val mockTS = mock[TwitterService]
      val exception = new IllegalStateException("Oh no")
      mockTS.updateWithMediaFromUrl(any[String]) returns Future.failed(exception)

      val actor = TestActorRef(TwitterActor.props(mockTS))
      val id = Messages.Id(1, 2, 3)
      actor ! TweetUploadRequest(id, "input")

      there was one(mockTS).updateWithMediaFromUrl("input")

      expectMsg(TweetFailure(id, exception))
    }
  }
}

