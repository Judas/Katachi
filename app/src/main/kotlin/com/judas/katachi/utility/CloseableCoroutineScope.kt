package com.judas.katachi.utility

import com.judas.katachi.utility.Logger.Level.ERROR
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    override fun close() = coroutineContext.cancelChildren()
}

val appScope = CloseableCoroutineScope(
    SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Logger.log(ERROR, "AppCoroutineScope", "Error caught in handler", throwable)
    })

fun ioScope(block: suspend CoroutineScope.() -> Unit) =
    appScope.launch(Dispatchers.IO, block = block)

fun uiScope(block: suspend CoroutineScope.() -> Unit) =
    appScope.launch(Dispatchers.Main.immediate, block = block)
