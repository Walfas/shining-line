package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker

import akka.testkit.{TestActorRef, TestProbe}
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.specification.Scope
import org.specs2.specification.SpecificationStructure
import scala.concurrent.Future
import scala.concurrent.duration._

class ManagerActorSpec extends Specification with Mockito with ActorSpecHelpers {
  "ManagerActor" >> {
    import ManagerActor._

    class TestCase extends Specification {
      val dbActor = TestProbe()
      val twitterActor = TestProbe()
      val actor = TestActorRef[ManagerActor](ManagerActor.props(dbActor.ref, twitterActor.ref))
      val actorU = actor.underlyingActor
    }

    inline(new TestCase {
      val id = Messages.Id(0, 0, 0)
      actor ! StickerRequest(id)

      "when receiving StickerRequest" should {
        "add the request to the pending map" in {
          actorU.pending must haveKey(id)
        }

        "send a StickerFindRequest to dbActor" in new ActorSpec {
          dbActor.expectMsg(DBActor.StickerFindRequest(id))
        }
      }
    })

    inline(new TestCase {
      "when receiving multiple StickerRequests for the same ID" should {
        val id = Messages.Id(0, 0, 0)
        val nTimes: Int = 5
        val duration = Duration(100, MILLISECONDS)

        val probe = TestProbe()
        for (_ <- 1 to nTimes) {
          actor.tell(StickerRequest(id), probe.ref)
        }

        "add the request to the pending map" in {
          actorU.pending must haveKey(id)
        }

        "have only one request in the pending map" in {
          actorU.pending.toList must have size(1)
        }

        "send only one StickerFindRequest to dbActor" in new ActorSpec {
          dbActor.expectMsg(DBActor.StickerFindRequest(id))
          dbActor.expectNoMsg(duration)
        }

        "senders should receive all the messages" in new ActorSpec {
          val response = mock[StickerResponse]
          actorU.completeRequest(id, response)

          probe.receiveN(5, duration).forall { msg => msg must_== response }

          probe.expectNoMsg(duration)
        }
      }
    })

    inline(new TestCase {
      val id = Messages.Id(0, 0, 0)
      actor ! StickerRequest(id)
      actorU.pending must haveKey(id)

      "when receiving RemoveFromPending" should {
        actor ! RemoveFromPending(id)

        "remove the request from the pending map" in {
          actorU.pending must not haveKey(id)
        }
      }
    })

    inline(new TestCase {
      "when receiving DBActor.StickerFound" should {
        "send StickerSuccess" in new ActorSpec {
          val id = Messages.Id(0, 0, 0)
          actor ! StickerRequest(id)

          val sticker = Sticker(0, 0, 0, "Hello")

          actor ! DBActor.StickerFound(id, sticker)
          expectMsg(StickerSuccess(sticker))
        }
      }

      "when receiving DBActor.StickerNotFound" should {
        "send TweetUploadRequest to twitterActor" in new ActorSpec {
          val id = Messages.Id(0, 0, 1)
          actor ! StickerRequest(id)

          actor ! DBActor.StickerNotFound(id, "Line URL")
          twitterActor.expectMsg(TwitterActor.TweetUploadRequest(id, "Line URL"))
        }
      }

      "when receiving DBActor.StickerFailure" should {
        "send StickerFailure" in new ActorSpec {
          val id = Messages.Id(0, 0, 2)
          actor ! StickerRequest(id)

          val exception = new IllegalArgumentException("Oh no")
          actor ! DBActor.StickerFailure(id, exception)

          expectMsg(StickerFailure(exception))
        }
      }

      "when receiving TwitterActor.TweetSuccess" should {
        "send StickerSuccess" in new ActorSpec {
          val id = Messages.Id(0, 0, 3)
          actor ! StickerRequest(id)

          actor ! TwitterActor.TweetSuccess(id, "Twitter URL")
          val sticker = Sticker(0, 0, 3, "Twitter URL")
          expectMsg(StickerSuccess(sticker))
        }
      }

      "when receiving TwitterActor.TweetFailure" should {
        "send StickerFailure" in new ActorSpec {
          val id = Messages.Id(0, 0, 4)
          actor ! StickerRequest(id)

          val exception = new IllegalArgumentException("Oh no")
          actor ! TwitterActor.TweetFailure(id, exception)

          expectMsg(StickerFailure(exception))
        }
      }
    })
  }
}



