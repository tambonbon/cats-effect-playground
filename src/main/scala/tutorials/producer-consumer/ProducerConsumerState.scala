package tutorials.`producer-consumer`

import cats.effect.{Deferred, Ref, Async}
import cats.effect.std.Console
import cats.syntax.all._
import scala.collection.immutable.Queue
import cats.effect.kernel.Sync

/** Previously, we used `Ref` to protect access to the queue .. .. and `Option` to represent
  * elements read from a possibly empty queue.. .. We should instead block the caller fiber somehow
  * if queue is empty .. .. until some element can be returned
  *
  * `Deferred` solves this. Deferred[F,A] instance can hold one single element of some type `A`
  * Deferred instances are created empty, and can be filled only once.. .. if some fibers tries to
  * read the element from an empty Deferred
  * ---> it will be sementically blocked until some other fiber completes it
  */
trait ProducerConsumerState {

  /** Alongside the queue of produced but not yet consumed elements.. .. we have to keep track of
    * `Deferred` instances created when queue was empty.. .. that are waiting for elements to be
    * available. These instances will be kept in a new queue `takers`
    *
    * We keep both queues in a new type State
    */
  case class State[F[_], A](queue: Queue[A], takers: Queue[Deferred[F, A]])
  
  object State {
    def empty[F[_], A]: State[F, A] = State(Queue.empty, Queue.empty)
  }

  /** Consumer works as follows:
    *   1. If `queue` is not empty ---> extract and return its head. The new state will keep the
    *      tail of the queue, not change on `takers` will be needed. 
    *   2. If `queue` is empty ---> use a new `Deferred` instance as a new `taker`, 
    *     add it to the `takers` queue, and 'block' the caller by `taker.get`
    */
  def consumer[F[_]: Async: Console](id: Int, stateR: Ref[F, State[F, Int]]): F[Unit] = { 
    // id is only used to identify the consumer in console logs
    val take: F[Int] = // implements the checking and updating of the state in stateR
      Deferred[F, Int].flatMap { taker =>
        stateR.modify {
          case State(queue, takers) if queue.nonEmpty =>
            val (i, rest) = queue.dequeue
            State(rest, takers) -> Async[F].pure(i) // Got element in queue, we can just return it
          case State(queue, takers) =>
            State(
              queue,
              takers.enqueue(taker)
            ) -> taker.get // No element in queue, must block caller until some is available
        }.flatten
      }

    for {
      i <- take
      _ <-
        if (i % 10000 == 0) Console[F].println(s"Consumer $id has reached $i items")
        else Async[F].unit
      _ <- consumer(id, stateR) 
    } yield ()
  }

  /**
   * Producer will:
     1. If there are waiting `taker` --> take the first in the queue and offer it newly produced element (taker.complete)
     2. If no `takers` are present, it will just enqueue the produced element
   */
  def producer[F[_]: Sync: Console](id: Int, counterR: Ref[F, Int], stateR: Ref[F, State[F,Int]]): F[Unit] = {

    def offer(i: Int): F[Unit] =
      stateR.modify {
        case State(queue, takers) if takers.nonEmpty =>
          val (taker, rest) = takers.dequeue
          State(queue, rest) -> taker.complete(i).void
        case State(queue, takers) =>
          State(queue.enqueue(i), takers) -> Sync[F].unit
      }.flatten

    for {
      i <- counterR.getAndUpdate(_ + 1)
      _ <- offer(i)
      _ <- if(i % 10000 == 0) Console[F].println(s"Producer $id has reached $i items") else Sync[F].unit
      _ <- producer(id, counterR, stateR)
    } yield ()
  }


}
