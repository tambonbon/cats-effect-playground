package tutorials.`producer-consumer`

import cats.effect._
import cats.effect.std.Console
import cats.syntax.all._
import collection.immutable.Queue

object ProducerConsumer {

  /** Access to the queue will be concurrent --> use `Ref` to:
    *   1. protect the queue so only one fiber at a time is handling it 2. ensure an ordered access
    *      to some shared data
    *
    * A `Ref` instance wraps given data and implements methods to manipulate that data in a safe
    * manner ** When some fiber is running on one of those methods, any other call to any method of
    * the `Ref` instance will be blocked **
    */

  def producer[F[_]: Sync: Console](queueR: Ref[F, Queue[Int]], counter: Int): F[Unit] =
    for {
      _ <-
        if (counter % 10000 == 0) Console[F].println(s"Produced $counter items") else Sync[F].unit
      _ <- queueR.getAndUpdate(_.enqueue(counter + 1))
      _ <- producer(queueR, counter + 1)
    } yield ()

}
