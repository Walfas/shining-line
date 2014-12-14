package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.services.TwitterService

import akka.testkit.TestActorRef
import org.specs2.mock._
import org.specs2.mutable._
import scala.concurrent.Future

class TwitterActorSpec extends Specification with Mockito with ActorSpecHelpers {
  "TwitterActor" >> {
    import TwitterActor._

    val mockTS = mock[TwitterService]
    val actor = TestActorRef(TwitterActor.props(mockTS))

    "when receiving TweetUploadRequest" should {
      "send TweetSuccess on successful" in new ActorSpec {
        mockTS.updateWithMediaFromUrl("success") returns Future.successful("output")

        val id = Messages.Id(1, 2, 3)
        actor ! TweetUploadRequest(id, "success")

        there was one(mockTS).updateWithMediaFromUrl("success")

        expectMsg(TweetSuccess(id, "output"))
      }

      "send TweetFailure on failure" in new ActorSpec {
        val exception = new IllegalStateException("Oh no")
        mockTS.updateWithMediaFromUrl("failure") returns Future.failed(exception)

        val id = Messages.Id(1, 2, 3)
        actor ! TweetUploadRequest(id, "failure")

        there was one(mockTS).updateWithMediaFromUrl("failure")

        expectMsg(TweetFailure(id, exception))
      }
    }
  }
}

