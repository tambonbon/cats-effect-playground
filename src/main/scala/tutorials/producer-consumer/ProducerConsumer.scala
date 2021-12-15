package tutorials.`producer-consumer`

import cats.effect._
import cats.effect.std.Console
import cats.syntax.all._
import collection.immutable.Queue

trait ProducerConsumer  {

  /** Access to the queue will be concurrent --> use `Ref` to:
    *   1. protect the queue so only one fiber at a time is handling it 2. ensure an ordered access
    *      to some shared data
    *
    * A `Ref` instance wraps given data and implements methods to manipulate that data in a safe
    * manner
    * ----> ** When some fiber is running on one of those methods, any other call to any method of
    * the `Ref` instance will be blocked ** <----
    *
    * The Ref wrapping our queue will be `Ref[F, Queue[Int]]` (for some `F[_]`)
    */

  // Producers write data one at a time to the queue
  def producer[F[_]: Sync: Console](queueR: Ref[F, Queue[Int]], counter: Int): F[Unit] =
    for {
      _ <-
        if (counter % 10000 == 0) Console[F].println(s"Produced $counter items") else Sync[F].unit
        // Typeclass Console[_]: capacity to print and read strings
      _ <- queueR.getAndUpdate(_.enqueue(counter + 1))
        // .getAndUpdate provides current queue, then we use .enqueue to insert next value counter+1
        // this call returns a new queue with value added that is stored by the ref instance
        // if some other fiber is accessing to `queueR` --> fiber is blocked
      _ <- producer(queueR, counter + 1)
    } yield ()

  // Consumer extract data from the queue, and it must be aware that the queue is empty
  def consumer[F[_]: Sync: Console](queueR: Ref[F, Queue[Int]]): F[Unit] =
    for {
    iO <- queueR.modify{ queue => // modify allows to modify wrapped data (or queue) and return a value that is computed from that data
      queue.dequeueOption.fold((queue, Option.empty[Int])){case (i,queue) => (queue, Option(i))}
    } // here, it will return Option[Int] that will be None if queue was empty
    _ <- if(iO.exists(_ % 10000 == 0)) Console[F].println(s"Consumed ${iO.get} items") else Sync[F].unit
    _ <- consumer(queueR)
  } yield ()

}
