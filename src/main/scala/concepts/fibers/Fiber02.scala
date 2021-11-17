package concepts.fibers

import cats.effect.IOApp
import cats.effect.IO

object Fiber02 extends IOApp.Simple {
  
    /* Every app has a "main fiber"..
    .. which is very similar to "main thread"
    This main fiber is defined using IOApp */

    val run = IO.println("Hello") >> IO.println("World")
    // in this example, Fiber02 is a main class
}
