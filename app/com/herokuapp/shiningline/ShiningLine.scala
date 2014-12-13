package com.herokuapp.shiningline

import com.typesafe.config.{Config, ConfigFactory}
import play.api.GlobalSettings
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object ShiningLine extends GlobalSettings {
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

