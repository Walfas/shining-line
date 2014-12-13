package com.herokuapp.shiningline.controllers

import com.herokuapp.shiningline.ShiningLine
import com.herokuapp.shiningline.services._
import com.herokuapp.shiningline.models._

import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import twitter4j.Twitter

object Application
    extends ApplicationController
    with TwitterServiceComponentImpl {
  lazy val twitter: Twitter = ShiningLine.twitter
}

trait ApplicationController extends Controller {
  this: TwitterServiceComponent =>

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def getSticker(stickerVersion: Int, packageId: Int, stickerId: Int) = Action.async {
    val s = Sticker(
      stickerVersion = stickerVersion,
      packageId = packageId,
      stickerId = stickerId,
      url = "http://example.com/sticker.png")
    Future.successful(Ok(Json.toJson(s)))
  }
}

