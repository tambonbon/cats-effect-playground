package concepts.Ref_Concurrent.typeclasses

import cats.effect.kernel.Ref
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.ExitCode

// https://blog.softwaremill.com/cats-concurrency-basics-with-ref-and-deferred-399e0335630
object Ref_01 extends IOApp {

  /** Ref is similar to Java atomic reference, but
    *   - Ref is used with tagless final abstraction F
    *   - Ref always contains a value
    *   - The value contained in Ref is always immutable
    */

  /** Ref[F[_], A]: "purely funcitonal mutable reference"
    *   - concurrent
    *   - lock free
    *   - always contain a value
    */
  def run(args: List[String]): IO[ExitCode] =
    for {
      intRef <- Ref.of[IO, Int](10)
      ten    <- intRef.get
      _      <- IO { println(s"Current value is $ten") }
      _      <- intRef.update(_ + 1) // way to update when execute both get and set
      current <- intRef.get
      _      <- IO { println(s"Current value is $current") }
    } yield ExitCode.Success

    /**
     * Drawbacks: If updating fails the function passed to update/modify needs to run non-deterministically, multiple times
     * Advantages: Much faster than standard lockijng and sync mechanism; Much safer as it cannot deadlock
     */
}
