package com.herokuapp.shiningline

import com.herokuapp.shiningline.actors._
import com.herokuapp.shiningline.models._
import com.herokuapp.shiningline.services._

import akka.actor.{ActorRef, ActorSystem}
import akka.routing.FromConfig
import com.typesafe.config.{Config, ConfigFactory}
import julienrf.play.jsonp.Jsonp
import play.api.Play.current
import play.api.db.slick.DB
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.WithFilters
import scala.slick.jdbc.JdbcBackend.Database
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object ShiningLine extends WithFilters(new Jsonp) {
  lazy val config: Config = ConfigFactory.load

  lazy val timeout: Int = config.getInt("application.timeout")

  // Define service dependencies
  lazy val twitter: Twitter = {
    val c: Config = config.getConfig("twitter.oauth")

    val cb: ConfigurationBuilder = new ConfigurationBuilder()
      .setOAuthConsumerKey(c.getString("consumerKey"))
      .setOAuthConsumerSecret(c.getString("consumerSecret"))
      .setOAuthAccessToken(c.getString("accessToken"))
      .setOAuthAccessTokenSecret(c.getString("accessTokenSecret"));

    val twitter: Twitter = new TwitterFactory(cb.build).getInstance
    twitter
  }

  lazy val dao: DAO = new DAO(DB.driver)

  lazy val lineUrl: String = config.getString("line.baseUrl")

  // Define services
  lazy val twitterService: TwitterServiceImpl = new TwitterServiceImpl(twitter)
  lazy val stickersService: StickersService = new SlickStickersService(DB, dao, lineUrl)

  // Define actors
  lazy val system: ActorSystem = Akka.system
  lazy val twitterActor: ActorRef = system.actorOf(TwitterActor.props(twitterService).withRouter(FromConfig), "twitter")
  lazy val dbActor: ActorRef = system.actorOf(DBActor.props(stickersService).withRouter(FromConfig), "db")
  lazy val managerActor: ActorRef = system.actorOf(ManagerActor.props(dbActor, twitterActor).withRouter(FromConfig), "manager")
}

