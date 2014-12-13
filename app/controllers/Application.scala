package controllers

import play.api._
import play.api.mvc._

import services.TwitterServiceComponentImpl
import scala.util.{Try, Success, Failure}

object Application extends Controller {
  val t = new TwitterServiceComponentImpl

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def index = Action.async {
    val f = t.twitterService.updateWithMediaFromUrl("http://dl.stickershop.line.naver.jp/products/0/0/1/3436/android/stickers/2487530.png")
      f.map{ x=> Ok(x) }.recover{ case e => Ok(e.toString) }
  }

}
