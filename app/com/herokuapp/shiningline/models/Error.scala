package com.herokuapp.shiningline.models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

// See http://jsonapi.org/format/#errors

case class Error(
  status: String = "500",
  detail: String = "An error has occurred")

object Error {
  implicit val errorFormat: Format[Error] = Json.format[Error]
}

