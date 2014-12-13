package com.herokuapp.shiningline

import com.typesafe.config.{Config, ConfigFactory}
import julienrf.play.jsonp.Jsonp
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.WithFilters
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object ShiningLine extends WithFilters(new Jsonp) {
  lazy val config: Config = ConfigFactory.load

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
}

