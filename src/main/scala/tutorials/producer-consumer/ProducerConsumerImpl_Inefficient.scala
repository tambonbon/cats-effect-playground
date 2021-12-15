package tutorials.`producer-consumer`

import cats.effect.IOApp
import cats.effect._
import cats.effect.std.Console
import cats.syntax.all._
import collection.immutable.Queue

object ProducerConsumerImpl_Inefficient extends ProducerConsumer with IOApp {

    def run(args: List[String]): IO[ExitCode] =
      for {
        queueR <- Ref.of[IO, Queue[Int]](Queue.empty[Int])
        res <- (consumer(queueR), producer(queueR, 0))
          .parMapN((_, _) =>
            ExitCode.Success
          ) // Run producer and consumer in parallel until done (likely by user cancelling with CTRL-C)
          // parMapN creates and runs fibers that will run the `IO`s passed as params
          .handleErrorWith { t =>
            Console[IO].errorln(s"Error caught: ${t.getMessage()} ").as(ExitCode.Error)
          }
      } yield res
      // `parMapN` promotes any error it finds & takes care of cancelling other running fibers
      // ---> use `parMapN` or `parSequence` to deal with fibers..
      // .. but far from ideal
      // If we run it we'll find producer runs faster than consumer so queue is constantly growing..
      // .. or consumer keeps running regardless there's no elements in the queue
      // ---> use Deferred
  
}

object ProducerConsumerImpl_Inefficient_02 extends ProducerConsumer with IOApp {
    def run(args: List[String]): IO[ExitCode] = 
      for {
        queueR <- Ref.of[IO, Queue[Int]](Queue.empty[Int])
        producerFiber <- producer(queueR, 0).start // explicitly create new Fiber instances
        consumerFiber <- consumer(queueR).start
        _ <- producerFiber.join // to wait for them to finish
        _ <- consumerFiber.join
      }  yield ExitCode.Error
      // this is not advisable to handle fibers manually as they are not trivial to work with
      // i.e. if there's an error in a fiber, the `join` will not raise it
  
}
