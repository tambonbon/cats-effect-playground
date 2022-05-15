package gettingStarted

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.ExitCode

object UnderstandProblem {
  // Side effects, i.e. reading/writing from db, console... ---> code difficult to refactor
  //    say, we have the following:
  def double(value: Int): Int = value * 2
  def doubleImpure(valut: Int): Int = {
    println(s"Double $valut")
    valut * 2
  }

  def main(args: Array[String]): Unit = {
    double(2) + double(2) + double(2)
    // the above expression can be easily refactored to:
    val twoDoubled = double(2)
    val sum        = twoDoubled + twoDoubled + twoDoubled

    println("Inlined version")
    doubleImpure(2) + doubleImpure(2) + doubleImpure(2)
    // trying to refactor doubleImpure
    println("Refactored version")
    val twoDoubledImpure = doubleImpure(2)
    val sumImpure        = twoDoubledImpure + twoDoubledImpure + twoDoubledImpure
  }
}

object HowIOHelp extends IOApp.Simple {
  // now we are using IO for our double function
  def double(value: Int): IO[Int] = {
    IO(println(s"Double $value")).map(_ => value * 2)
  }

  def run = {
    println("Inlined version")
    val sumIO = for {
      first <- double(2)
      second <- double(2)
      third <- double(2)
    } yield first + second + third
    // We still need to call the effect out

    println("Refactored version")
    val twoDouble = double(2)
    val sumIO2 = for {
      first <- twoDouble
      second <- twoDouble
      third <- twoDouble
    } yield first + second + third

    sumIO.flatMap(IO.println(_)).flatMap(sumIO2 => IO.println(sumIO2))

    // sumIO2.flatMap(IO.println(_))
  }
}
