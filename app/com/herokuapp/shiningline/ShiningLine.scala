package com.herokuapp.shiningline

import com.herokuapp.shiningline.models._
import com.herokuapp.shiningline.services._

import com.typesafe.config.{Config, ConfigFactory}
import julienrf.play.jsonp.Jsonp
import play.api.Play.current
import play.api.db.slick.DB
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.WithFilters
import scala.slick.jdbc.JdbcBackend.Database
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object ShiningLine extends WithFilters(new Jsonp) {
  lazy val config: Config = ConfigFactory.load

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
}

