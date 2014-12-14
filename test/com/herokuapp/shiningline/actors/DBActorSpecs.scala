package com.herokuapp.shiningline.actors

import com.herokuapp.shiningline.models.Sticker
import com.herokuapp.shiningline.services.StickersService

import akka.testkit.TestActorRef
import org.specs2.mock._
import org.specs2.mutable._
import scala.concurrent.Future

class DBActorSpec extends Specification with Mockito with ActorSpecHelpers {
  "DBActor" >> {
    import DBActor._

    val mockSS = mock[StickersService]
    val actor = TestActorRef(DBActor.props(mockSS))

    "when receiving StickerFindRequest" >> {
      "send StickerFound on successful find" in new ActorSpec {
        val sticker = Sticker(0, 0, 0, "Hello")
        mockSS.find(0, 0, 0) returns Some(sticker)

        val id = Messages.Id(0, 0, 0)
        actor ! StickerFindRequest(id)

        there was one(mockSS).find(0, 0, 0)

        expectMsg(StickerFound(id, sticker))
      }

      "send StickerNotFound on unsuccessful find" in new ActorSpec {
        mockSS.find(0, 0, 1) returns None
        mockSS.getUrl(0, 0, 1) returns "URL of sticker"

        val id = Messages.Id(0, 0, 1)
        actor ! StickerFindRequest(id)

        there was one(mockSS).find(0, 0, 1)

        expectMsg(StickerNotFound(id, "URL of sticker"))
      }

      "send StickerFailure on exception" in new ActorSpec {
        val exception = new IllegalArgumentException("Oh no")
        mockSS.find(0, 0, 2) throws exception

        val id = Messages.Id(0, 0, 2)
        actor ! StickerFindRequest(id)

        there was one(mockSS).find(0, 0, 2)

        expectMsg(StickerFailure(id, exception))
      }
    }

    "when receiving StickerSaveRequest" should {
      "save the sticker" in new ActorSpec {
        val sticker = Sticker(0, 0, 0, "Save this")

        actor ! StickerSaveRequest(sticker)

        there was one(mockSS).save(sticker)
      }
    }
  }
}


