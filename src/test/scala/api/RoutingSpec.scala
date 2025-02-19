package api

import api.Routing._
import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s.Method.POST
import org.http4s.implicits._
import org.http4s.{Request, Status}
import sql.AnalysisError._

class RoutingSpec extends CatsEffectSuite {
  private val baseRequest = Request[IO](method = POST, uri = uri"/extractTableNames")

  test("Routing forwards the input as-is to the other layer, returns Ok if a Right is returned") {
    // given
    val ProvidedInput = "mock input"
    val ResultContent = "mock result"
    val inputDependantFn = (input: String) =>  input match {
      case ProvidedInput => Right(Set(ResultContent))
      case _ => Left(InternalError)
    }
    val routes = new Routing(inputDependantFn).routes[IO]
    val request = baseRequest.withEntity(ProvidedInput)
    // when
    routes.orNotFound(request).flatMap { response =>
      // then
      assertEquals(response.status, Status.Ok)
      response.as[String].map { body =>
        assertEquals(body, s"[\"$ResultContent\"]")
      }
    }
  }

  test("Routing should return BadRequest for invalid input") {
    // given
    val invalidInputFn = (_: String) => Left(InvalidInputError)
    val routes = new Routing(invalidInputFn).routes[IO]
    // when
    routes.orNotFound(baseRequest).flatMap { response =>
      // then
      assertEquals(response.status, Status.BadRequest)
      response.as[String].map { body =>
        assertEquals(body, InvalidInputMessage)
      }
    }
  }

  test("Routing should return InternalServerError for internal errors") {
    // given
    val internalErrorFn = (_: String) => Left(InternalError)
    val routes = new Routing(internalErrorFn).routes[IO]
    // when
    routes.orNotFound(baseRequest).flatMap { response =>
      // then
      assertEquals(response.status, Status.InternalServerError)
      response.as[String].map { body =>
        assertEquals(body, InternalServerErrorMessage)
      }
    }
  }
}
