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
      "when receiving StickerRequest" should {
        val id = Messages.Id(0, 0, 0)
        actor ! StickerRequest(id)

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

        for (_ <- 1 to nTimes) {
          actor ! StickerRequest(id)
        }

        "add the request to the pending map" >> {
          actorU.pending must haveKey(id)
        }

        "have only one request in the pending map" >> {
          actorU.pending.toList must have size(1)
        }

        "send only one StickerFindRequest to dbActor" >> new ActorSpec {
          dbActor.expectMsg(DBActor.StickerFindRequest(id))
          dbActor.expectNoMsg(Duration(100, MILLISECONDS))
        }
      }
    })
  }
}



