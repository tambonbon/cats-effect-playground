package concepts.fibers

import scala.concurrent.duration._
import cats.effect.IO

object Fiber03 {
  
    /** 
     * Fibers are **cancelable** at all points during their exec
     * --> all unneeded calculations can be promptly terminated
     * 
     * Fiber cancelation most often happens in response to one of two situations:
         1.timeouts
         2. concurrent errors
     *
    */

    lazy val loop: IO[Unit] = IO.println("Hello, World!") >> loop

    loop.timeout(5.seconds)
    // The above constructs an IO starting a fiber defined by `loop`
    // This fiber prints Hello World infinitely to standard out
    // However, `timeout` funciton delays for 5 secs, after which it calls `cancel` on the fiber
    // .. interrupting its execution and freeing resources it currently holds
}
