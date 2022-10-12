package gettingStarted.adamRosien

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.concurrent.Await
import scala.util.Success

object Future01 {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]): Unit = {  
    val twice = Future(println("Hello World!")).flatMap(_ => Future(println("Hello World!")))
    Thread.sleep(1000) // print 2 hello worlds

    // start refactor
    val print = Future(println("Hello World!"))
    val twice02 = print.flatMap(_ => print)
    Thread.sleep(500) // print only 1 hello world
  }
}
