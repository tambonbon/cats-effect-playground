package concepts.fibers

import cats.effect.IO
import cats.effect.IOApp

object Fiber01 {
  
    /** 
    * Fibers are fundamental abstraction in Cats Effect
    * a lightweight thread, like "coroutines"
    * Much like threads, they represent a sequence of actions..
    * .. which will ultimatey be evaluated in that order
    * 
    * Fiber != Threads: their footprint and level of abstraction
    * 
    * 1. Fiber are **very** lightweight
    * As an example, any client/server application defined using Cats Effect will create a new fiber for each inbound request
    * ---> creating and starting a new fiber is extremely fast in and of itself
    * 
    * Cats Effect takes this concept even further by defining first-class support for asynchronous callbacks,
    * ..resource handling, and cancelation (interruption) for all fibers.
    * 
    * Cats Effect takes this concept even further by defining first-class support
    * ..for asynchronous callbacks, resource handling, and cancelation (interruption) for all fibers
    * Any individual "step" of a fiber (much like a statement in a thread) may be
    * .. either synchronous in that it runs until it produces a value or errors,
    * .. or asynchronous in that it registers a callback which may be externally invoked at some later point,
    * .. and there is no fundamental difference between these steps: they're just part of the fiber. 
    * 
    * ----> Put another way: with fibers, there is no difference between a callback and a return.
    */

    
    def fiberConcept(args: Array[String]): Unit = {
    
     /**
      * Each step in a thread is a "statement" 
      * .. those statements are defined in sequence by writing them in a particular order
      * Each step in a fiber is an "effect"
      * .. those effects are defined in sequence by explicitly composing them using `flatMap`
      *
      */ 
       IO.println("Hello") flatMap(_ => IO.println("World")) 

       // or we can use for-expr equivalently
       for {
           _ <- IO.println("Hello")
           _ <- IO.print("World")
       } yield ()

    //    the pattern where we put together 2 effects & ignore result of the 1st one: >>
        IO.println("Hello") >> IO.println("World")
    }
}
