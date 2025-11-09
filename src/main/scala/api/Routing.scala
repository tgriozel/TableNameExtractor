package api

import api.Routing._
import cats.effect.Concurrent
import cats.syntax.all._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import sql.AnalysisError
import sql.AnalysisError._

object Routing {
  private type ProcessingResultT = Either[AnalysisError, Set[String]]
  private type ProcessingFnT = String => ProcessingResultT
  private val ProcessingEndpointName = "extractTableNames"
  val InvalidInputMessage = "Invalid input"
  val InternalServerErrorMessage = "Internal server error"
}

class Routing(processingFn: ProcessingFnT) {
  private def processInput[F[_]](input: String)(using Concurrent[F]): F[ProcessingResultT] =
    Concurrent[F].pure(processingFn(input))

  def routes[F[_]](using Concurrent[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case request @ POST -> Root / ProcessingEndpointName =>
        for {
          input <- request.as[String]
          result <- processInput(input)
          output <- result match {
            case Right(result) => Ok(result.asJson)
            case Left(InvalidInputError) => BadRequest(InvalidInputMessage)
            case Left(InternalError) => InternalServerError(InternalServerErrorMessage)
          }
        } yield output
    }
  }
}
