package util

import play.api.libs.json.{Format, JsValue, Json}

trait SimpleJsonParser {
  def jsonString2T[T](str: String)(implicit format: Format[T]): T = Json.fromJson[T](Json.parse(str))
    .getOrElse(throw new RuntimeException(s"Could not parse String $str or create T from json value"))

  private def jsonValue2T[T](json: JsValue)(implicit format: Format[T]): Option[T] = Json.fromJson[T](json).asOpt

  def t2JsonString[T](t: T)(implicit format: Format[T]): String = Json.toJson[T](t).toString()
}
