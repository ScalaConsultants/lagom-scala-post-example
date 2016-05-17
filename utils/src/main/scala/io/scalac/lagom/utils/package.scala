package io.scalac.lagom

package object utils {

  case class ServerError(msg: String) extends Exception(msg)

}
