package tutorials.`producer-consumer`

import cats.effect._
import cats.effect.std.Console
import cats.instances.list._
import cats.syntax.all._
import scala.collection.immutable.Queue

object ProducerConsumerImpl_Efficient extends ProducerConsumerState with IOApp {

    def run(args: List[String]): IO[ExitCode] =
      for {
        stateR   <- Ref.of[IO, State[IO, Int]](State.empty[IO, Int])
        counterR <- Ref.of[IO, Int](1)
        producers = List.range(1, 11).map(producer(_, counterR, stateR)) // 10 producers
        consumers = List.range(1, 11).map(consumer(_, stateR))           // 10 consumers
        res <- (producers ++ consumers).parSequence
          .as(
            ExitCode.Success
          ) // Run producers and consumers in parallel until done (likely by user cancelling with CTRL-C)
          .handleErrorWith { t =>
            Console[IO].errorln(s"Error caught: ${t.getMessage}").as(ExitCode.Error)
          }
      } yield res
}
