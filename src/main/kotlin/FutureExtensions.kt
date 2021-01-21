import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Future
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Future<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        cont.invokeOnCancellation { this.cancel(true) }
        runCatching {
            get()
        }.onSuccess {
            cont.resume(it)
        }.onFailure {
            cont.resumeWithException(it)
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> Future<T>.asFlow(): Flow<T> = callbackFlow {
    invokeOnClose { this@asFlow.cancel(true) }
    runCatching {
        get()
    }.onSuccess {
        sendBlocking(it)
    }.onFailure {
        throw it
    }
}