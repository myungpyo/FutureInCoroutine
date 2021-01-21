import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class FutureAwaitTester {
    companion object {
        @JvmStatic
        fun main(args: Array<String>): Unit = runBlocking {
            val executor = Executors.newCachedThreadPool()
            val future = executor.submit(HeavyTask())

            val testingJob = GlobalScope.launch {
                val futureJob = launch {
                    runCatching {
                        future.await()
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