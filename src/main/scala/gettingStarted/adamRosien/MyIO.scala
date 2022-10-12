package gettingStarted.adamRosien

import scala.concurrent.Future

// To delay safe effects --> create a wrapper for the side-effects
case class MyIO[A](unsafeRun: () => A) { // this MyIO effect is gonna produce a value at the end of type A
  // --> guarantee that we don't run things now
  def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
  def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
}

object MyIO {
  def main(args: Array[String]): Unit = {
    val print   = MyIO(() => println("Hello World!"))
    val twice02 = print.flatMap(_ => print) // nothing is executed here; we're only declaring what will happen
    twice02.unsafeRun() // unsafeRun actually executes the declared effects
    // Thread.sleep(500)
  }
}
