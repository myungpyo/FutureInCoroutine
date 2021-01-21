import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executors

class FutureAsFlowTester {
    companion object {
        @ExperimentalCoroutinesApi
        @JvmStatic
        fun main(args: Array<String>): Unit = runBlocking {
            val executor = Executors.newCachedThreadPool()
            val future = executor.submit(HeavyTask())

            val testingJob = GlobalScope.launch {
                val futureJob = launch {
                    runCatching {
                        future.asFlow().collect()
                    }.onSuccess { value ->
                        println("Success : value = $value")
                    }.onFailure { throwable ->
                        println("Fail : throwable = $throwable")
                    }
                }

                launch {
                    // Request cancel
                    delay(5_000)
                    futureJob.cancel()
                }
            }

            testingJob.join()
            executor.shutdownNow()
        }
    }
}