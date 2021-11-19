package tutorials.`producer-consumer`

object IntroToFiber {
  
  /**
   * A fiber carries an `F` action to execute (typically an `IO` instance)
   * 
   * Fibers are 'light' threads --> they can be used in similar way than threads to create concurrent code
   * BUT THEY ARE NOT THREADS
   * --> 1. Spawning new fibers NOT guarantee the action described in `F` associated to it will be run if shortage of threads
   * ---> a. If no thread in pool -> fiber execution will 'wait' until some thread is free again
   *      b. If execution of some fiber is blocked e.g. bc it must wait for a semaphore to be released 
   *        --> thread running the fiber is recycled by cats-effect so it's available for other fibers
   *          ---> "semantically blocked": blocking the fiber NOT involve halting any thread
   * --> 2. Fibers are very cheap entities
   * 
   */
}
