package com.hunorkovacs.itsaren.simple.http

import cats.data.Kleisli
import cats.effect.IO
import com.hunorkovacs.itsaren.simple.arn.{ArnDb, ArnNoId}
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}
import java.util.UUID
import cats.effect.kernel.Ref
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j._

object HttpRouter:

  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory[IO]
  val logger: SelfAwareStructuredLogger[IO]     = LoggerFactory[IO].getLogger

  def applicationRoutes(arnDb: ArnDb): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "v1" / "arns" =>
          arnDb.retrieveAll.flatMap(Ok(_))

        case GET -> Root / "v1" / "arns" / UUIDVar(id) =>
          arnDb.find(id).flatMap {
            case None      => NotFound(HttpMessage("Arn not found"))
            case Some(arn) => Ok(arn)
          }

        case req @ POST -> Root / "v1" / "arns" =>
          for {
            arnNoId <- req.as[ArnNoId]
            arn     <- arnDb.create(arnNoId)
            resp    <- Ok(arn)
          } yield resp

        case req @ PUT -> Root / "v1" / "arns" / UUIDVar(id) =>
          for
            arnNoId <- req.as[ArnNoId]
            arn     <- arnDb.update(id, arnNoId)
            resp    <- arn match
                         case Some(updatedArn) => Ok(updatedArn)
                         case None             => NotFound(HttpMessage("Arn not found, can't update."))
          yield resp

        case DELETE -> Root / "v1" / "arns" / UUIDVar(id) =>
          arnDb.delete(id).flatMap {
            case None    => NotFound(HttpMessage("Arn not found, can't delete."))
            case Some(_) => NoContent()
          }
      }
      .orNotFound

  def healthRoutes(ready: Ref[IO, Boolean]): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "health" =>
          Ok(HttpMessage("healthy"))

        case GET -> Root / "is_initialized" =>
          Ok(HttpMessage("initialized"))

        case GET -> Root / "is_ready" =>
          ready.get.flatMap {
            case true  => Ok(HttpMessage("ready"))
            case false => NotFound(HttpMessage("not ready"))
          }
      }
      .orNotFound

  case class HttpMessage(message: String)

  implicit val arnNoIdDecoder: EntityDecoder[IO, ArnNoId] = jsonOf[IO, ArnNoId]
