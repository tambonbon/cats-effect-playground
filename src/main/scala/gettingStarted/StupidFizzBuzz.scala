package gettingStarted

import cats.effect.IOApp
import cats.effect.IO
import scala.concurrent.duration._

object StupidFizzBuzz extends IOApp.Simple {
  
    // runs four concurrent lightweight threads, or fibers, 
    // one of which counts up an Int value once per second, while the others poll that value for changes and print in response
    val run = 
        for {
            ctr <- IO.ref(0)

            wait = IO.sleep(1.second)
            poll = wait *> ctr.get

            _ <- poll.flatMap(IO.println(_)).foreverM.start
            _ <- poll.map(_ % 3 == 0).ifM(IO.println("fizz"), IO.unit).foreverM.start
            _ <- poll.map(_ % 5 == 0).ifM(IO.println("buzz"), IO.unit).foreverM.start
            
          _ <- (wait *> ctr.update(_ + 1)).foreverM.void
        } yield ()
}
