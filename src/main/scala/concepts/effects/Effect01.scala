package concepts.effects

import cats.effect.IO
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Effect01 extends App {
  
    /** 
     * Effect is an description of action(s) that will be taken when evaluation happens
     * 
    */

    // A common sort of effect is `IO`:
    val printer: IO[Unit] = IO.println("Hello, World") 
    val printAndRead: IO[String] = IO.print("Enter your name") >> IO.readLine
    // ^^^ the above effects describe an action(actions in printAndRead) ..
    // .. wich will be taken when they're evaluated

    def foo(str: String): IO[String] = ??? // a effectful function, returns an effect

    // `printer` is just a descriptive value; it NOT do anything on its own
    // ---> Hello World will be printed exactly ZERO times
    // this is somthing `Future` cannot do
    val printerfuture: Future[Unit] = Future(println("Hello, World"))

    class AdvancedCatsEffect {
        import cats.Monad
        import cats.effect.std.Console
        import cats.syntax.all._

        def example[F[_]: Monad: Console](str: String): F[String] = {
            val printer: F[Unit] = Console[F].println(str)
            (printer >> printer).as(str)
        }
        /** 
         * `example` is an effectful function
         * `printer` is an effect (as is printer >> printer)
         * `F` is effect "type", which might be `IO` or anything
         * 
         * The caller of example is free to choose the effect at the call site
         * e.g. example[IO]("Hello World") -> return an IO[String]
        */
    }

}
