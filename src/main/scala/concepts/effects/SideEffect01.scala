package concepts.effects

import cats.effect.IO

object SideEffect01 {
  
    /** 
     * When running a piece of code causes changes outside of just returning a value
     * ---> that code "has side-effects"
     * 
     * Effects != Side-effects
     * 1. Effect is a "description" of some actions, where the action ay perform S.E.
     *  -> safer, more controllable
     * 2. When S.E. is contained, that action just "happens"
     *  -> can't make it evaluate in parallel, or on diff thread pool, or on a schedule
     * 
     * In Cats Effect, code containing side-effects should always be wrapped in one of the "special" constructors:
         1. Synchronous (`return` or `throw`)
            - IO(...) or IO.delay(...)
            - IO.blocking(...)
            - IO.interruptible(true/false)
         2. Asynchronous (invokes a callback)
            - IO.async or IO.async_
     * When S.E. is wrapped in one of these constructors,
     * .. the code itself still contains S.E., but outside the lexical scope of the constructor 
     * .. we can reason about the whole thing as an effect, ratehr than as a S.E.
    */

    val wrapped: IO[Unit] = IO(System.out.println("Hello World"))

    /** Remarks:
     * Being strict about this rule of thumb 
     * .. and always wrapping your side-effecting logic in effect constructors 
     * .. unlocks all of the power and composability of functional programming
     */
}
