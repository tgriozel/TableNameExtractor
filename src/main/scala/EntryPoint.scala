import api.Routing
import cats.effect.IO
import cats.effect.IOApp.Simple
import org.http4s.blaze.server.BlazeServerBuilder
import sql.TablesFinder

object EntryPoint extends Simple {
  private val BindPort = 8080
  private val routing = new Routing(processingFn = TablesFinder.extractTableNames)

  override def run: IO[Unit] =
    BlazeServerBuilder[IO]
      .withExecutionContext(runtime.compute)
      .bindLocal(BindPort)
      .withHttpApp(routing.routes[IO].orNotFound)
      .serve
      .compile
      .drain
}
