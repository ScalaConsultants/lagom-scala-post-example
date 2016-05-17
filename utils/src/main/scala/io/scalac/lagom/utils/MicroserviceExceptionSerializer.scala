package io.scalac.lagom.utils

import java.util.Optional
import java.util.concurrent.CompletionException

import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.deser.{ExceptionSerializer, RawExceptionMessage}
import com.lightbend.lagom.javadsl.api.transport.{MessageProtocol, TransportErrorCode}

class MicroserviceExceptionSerializer extends ExceptionSerializer {
  override def serialize(exception: Throwable, accept: java.util.Collection[MessageProtocol]): RawExceptionMessage = {
    val mp = MessageProtocol.fromContentTypeHeader(Optional.empty())

    def defaultMessage = new RawExceptionMessage(
      TransportErrorCode.InternalServerError,
      mp,
      ByteString.apply(exception.getMessage)
    )

    def getMessageForCompletionException(completionEx: CompletionException) =
      completionEx.getCause match {
        case ex: ServerError =>
          new RawExceptionMessage(
            TransportErrorCode.InternalServerError,
            mp,
            ByteString.apply(ex.msg)
          )
        case ex => defaultMessage
      }

    exception match {
      case ex: CompletionException =>
        getMessageForCompletionException(ex)
      case ex: Throwable =>
        defaultMessage
    }
  }

  //let's leave it unimplemented since it's not required for our example to work
  override def deserialize(message: RawExceptionMessage): Throwable = ???
}
