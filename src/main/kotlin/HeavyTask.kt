import java.util.concurrent.Callable

class HeavyTask : Callable<String> {
    override fun call(): String {
        val currentThread = Thread.currentThread().toString()
        repeat(10) {
            println("HeavyTask is running ...$it")
            Thread.sleep(1_000)
        }
        return currentThread
    }
}