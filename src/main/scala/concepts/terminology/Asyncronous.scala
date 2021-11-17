package concepts.terminology

import cats.effect.IO

object Asyncronous {
  
    /** 
     * Opposite of "synchronous"
     * 
     * Syncronous effects are defined using `apply`, `delay` or `blocking`
     * .. and produce results using `return`,
     * .. or raise errors using `throw`
    */

    // These are the familiar "sequential" type of effects:
    IO(Thread.sleep(500)) // => IO[Unit]

    /** 
     * Asynchronous effects are defined using `async`, `async_)
     * .. and produce results using a callback
     * .. where a successful result is wrapped in `Right`
    */

    import java.util.concurrent.{Executors, TimeUnit}
    
    val scheduler = Executors.newScheduledThreadPool(1)

    IO.async_[Unit] { cb => 
        scheduler.schedule(new Runnable {
            def run = cb(Right(())) // cb is Either
        }, 500, TimeUnit.MILLISECONDS)
        ()
    }

    /**
      * Both `Thread.sleep` and `schedule` delay 500ms
      * .. before allowing the next step in fiber to take place
      * 
      * Difference:
          1. `Thread.sleep` NOT return JVM-level control flow until after its delay expires
          ---> waste a scarce resrc (underlying kernel Thread) for its full duration
          ----> prevent other actions from utilizing that resrc more efficiently in the interim
          2. `schedule` returns IMMEDIATELY when run and simply invokes the callback in future once given time has elapsed
          ---> underlying kernel `Thread` NOT wasted 

      * Fiber: built-in support for async effects
      * Both above examples, the effect is simply a value of type IO[Unit] 
      * 
      * Remarks: async NOT imply parallel or simultaneous, nor negate sequential (all fibers are sequences of effects)
      * ----> Asynchronous: "produces values/errors using a callback rather than `return/throw`"
      */
}
