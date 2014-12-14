package com.herokuapp.shiningline.actors

import akka.actor.ActorSystem
import akka.testkit._
import org.specs2.specification.Scope

object ActorSpecHelpers {
  val system: ActorSystem = ActorSystem("test")
}

trait ActorSpecHelpers {
  implicit val system: ActorSystem = ActorSpecHelpers.system

  class ActorSpec extends TestKit(system) with Scope with ImplicitSender
}

