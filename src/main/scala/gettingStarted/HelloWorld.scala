package gettingStarted

import cats.effect.{IO, IOApp}

object HelloWorld extends IOApp.Simple {
    
    // Applications written in this style have full access to timers, multithreading, 
    // and all of the bells and whistles that you would expect from a full application.
    val run = IO.println("Hello, World!")
}
