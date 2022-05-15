// package concepts.typeclasses.Deferred_Concurrent

// import cats.effect.kernel.Deferred
// import cats.effect.IO

// object Deferred_01 {

//   /** Deferred is
//     *   - created empty
//     *   - can be completed once
//     *   - once set, it cannot be modified or become empty again
//     *
//     * Deferred is for "purely functional synchronisation".. .. when call `get` on empty `Deferred`
//     * we block until the value is available .. when call `get` on non-empty `Deferred` we get
//     * immediately the value stored
//     *
//     * .. when call `complete` on empty `Deferred` we fill up the value .. when call `complete` on
//     * non-empty `Deferred` we get failure (failed IO in case of IO)
//     */

//   def consumer(done: Deferred[IO, Unit]) = for {
//     c   <- Consumer.setup
//     _   <- done.complete(())
//     msg <- c.read
//     _   <- IO(println(s"Received $msg"))
//   } yield ()

//   def producer(done: Deferred[IO, Unit]) = for {
//     p <- Producer.setup() 
//     _ <- done.get // 1. we want the producer to wait for the comsumer setup to finish (l23) ..
//     msg = "Msg A" 
//     _ <- p.write(msg) // 2. .. before writing the message, otherwise whatever we would write in producer will be lost
//     _ <- IO(println(s"Sent $msg")) // .. thats why we use shared Deferred instance and block on `get` (l30) .. 
//   } yield () // .. until the `done` `Deferred` instance will be filled up with value on the consumer side

//   def prog = for {
//     d <- Deferred[IO, Unit]
//     _ <- consumer(d).start
//     _ <- producer(d).start
//   } yield ()

//   object Consumer {
//     def setup = ???
//   }

// }
