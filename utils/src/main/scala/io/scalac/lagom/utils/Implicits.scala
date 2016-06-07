package io.scalac.lagom.utils

import java.util.concurrent.CompletableFuture._
import java.util.concurrent.CompletionStage

import com.lightbend.lagom.javadsl.api.ServiceCall

object Implicits {
  implicit def requestToServiceCallWithCompletedFuture[Req, Resp](reqFunc: Req => Resp): ServiceCall[Req, Resp] = {
    new ServiceCall[Req, Resp] {
      override def invoke(request: Req): CompletionStage[Resp] = {
        completedFuture(reqFunc(request))
      }
    }
  }

  implicit def requestToServiceCall[Req, Resp](reqFunc: Req => CompletionStage[Resp]): ServiceCall[Req, Resp] = {
    new ServiceCall[Req, Resp] {
      override def invoke(request: Req): CompletionStage[Resp] = {
        reqFunc(request)
      }
    }
  }

  implicit def sFun1ToAkkaJapiFun[T, R](sFun1: T => R): akka.japi.function.Function[T, R] =
    new akka.japi.function.Function[T, R] {
      @scala.throws[Exception](classOf[Exception])
      override def apply(param: T): R = sFun1.apply(param)
    }

  implicit def sFunToAkkaEffect(sFun: () => Unit): akka.japi.Effect =
    new akka.japi.Effect {
      @scala.throws[Exception](classOf[Exception])
      override def apply(): Unit = sFun()
    }

  implicit def asJavaBiFunction[T, U, R](sFun: (T, U) => R): java.util.function.BiFunction[T, U, R] =
    new java.util.function.BiFunction[T, U, R] {
      override def apply(t: T, u: U): R = sFun(t, u)
    }

  implicit def asJavaConsumer[T](sFun: T => Unit): java.util.function.Consumer[T] =
    new java.util.function.Consumer[T] {
      override def accept(t: T): Unit = sFun(t)
    }

  implicit def asJavaFunction[T, R](sFun: T => R): java.util.function.Function[T, R] =
    new java.util.function.Function[T, R] {
      override def apply(t: T): R = sFun(t)
    }

  implicit def asJavaBiConsumer[T, U](sFun: (T, U) => Unit): java.util.function.BiConsumer[T, U] =
    new java.util.function.BiConsumer[T, U] {
      override def accept(t: T, u: U): Unit = sFun(t, u)
    }
}
