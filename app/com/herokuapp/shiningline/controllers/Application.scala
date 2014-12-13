package com.herokuapp.shiningline.controllers

import com.herokuapp.shiningline.ShiningLine
import com.herokuapp.shiningline.services._

import play.api._
import play.api.mvc._
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

  def index = Action.async {
    val f = twitterService.updateWithMediaFromUrl("http://dl.stickershop.line.naver.jp/products/0/0/1/3436/android/stickers/2487530.png")
    f.map{ x => Ok(x) }.recover{ case e => throw e }
  }
}

