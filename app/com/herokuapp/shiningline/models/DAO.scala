package com.herokuapp.shiningline.models

import scala.slick.driver.JdbcProfile
import scala.slick.lifted.TableQuery
import play.api.db.slick.{Config, Profile}

class DAO(override val profile: JdbcProfile)
    extends StickerComponent
    with Profile

object current {
  val dao = new DAO(Config.driver)
}

