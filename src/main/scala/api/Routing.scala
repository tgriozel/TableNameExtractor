package api

import api.Routing._
import cats.effect.Concurrent
import cats.implicits._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import sql.AnalysisError._

object Routing {
  private type ProcessingFnType = String => Either[AnalysisError, Set[String]]
  private val ProcessingEndpointName = "extractTableNames"
  val InvalidInputMessage = "Invalid input"
  val InternalServerErrorMessage = "Internal server error"
}

class Routing(processingFn: ProcessingFnType) {
  private def processInput[F[_] : Concurrent](input: String): F[Either[AnalysisError, Set[String]]] =
    Concurrent[F].pure(processingFn(input))

  def routes[F[_] : Concurrent]: HttpRoutes[F] = {
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
