package com.nodrex.kotlincoroutines.coroutines

import com.nodrex.kotlincoroutines.log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.util.logging.Handler
import kotlin.coroutines.ContinuationInterceptor
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@OptIn(DelicateCoroutinesApi::class)
fun myThreadPool() {
    val myThreadPool = newFixedThreadPoolContext(1, "Thread_by_Nodrex")
    (1..20).forEach {
        CoroutineScope(myThreadPool).launch {
            log("Coroutine launched[$it]")
            delay(5.seconds)
            log("Coroutine finished[$it] " + Thread.currentThread().name)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun limitedDispatcher() {
    log("limitedDispatcher")
    val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)
    (1..20).forEach {
        CoroutineScope(limitedDispatcher).launch {
            delay(5.seconds)
            log("Coroutine finished[$it] " + Thread.currentThread().name)
        }
    }
}

fun stressTest() {
    CoroutineScope(Dispatchers.Main).launch {
        log("Coroutine on main dispatcher started.......")
        delay(10.seconds)
        log("Coroutine on main dispatcher ended")
    }
}

fun contextSwitch() {
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            log("Coroutine started...")
            delay(5.seconds)
            withContext(Dispatchers.Main) {
                log("Updating UI")
                delay(5.seconds)
            }
            log("Fetch data again")
        }
    }
}

fun contextSwitchOptimization() {
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            log("Coroutine started...")
            delay(5.seconds)
            launch(Dispatchers.Main) {
                log("Updating UI")
                delay(5.seconds)
            }
            log("Fetch data again")
        }
    }
}

fun coroutineCancellation() {
    val job = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            log("imitating permanent request to a server")
        }
    }
    job.cancel()
}

fun coroutineCancellationWithoutDirectCheck() {
    val job = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            log("imitating permanent request to a server")
            delay(1.seconds)
        }
    }
    job.cancel()
}

fun coroutineCancellationWithEnsureActive() {
    val job = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            log("imitating permanent request to a server")
            try {
                ensureActive()
            } catch (e: Exception) {
                log("Exception -> $e")
                if (e is CancellationException) {
                    throw e
                }
            }
        }
    }
    job.cancel()
}

fun coroutineCompletionDetection() {
    CoroutineScope(Dispatchers.IO).launch {
        (1..20).forEach {
            launch {
                delay(5.seconds)
                log("Coroutine finished[$it] " + Thread.currentThread().name)
            }
        }
    }.invokeOnCompletion { exception ->
        log("Parent Coroutine finished[$exception]")
    }
}

fun coroutineExceptionHandler() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val dispatcher = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        log("Exception was caught by exception handler! for dispatcher[$dispatcher] -> $throwable")
    }
    CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        throw Exception("Test Exception by Nodrex")
    }
}

fun joinCoroutine() {
    val managerWants = false
    CoroutineScope(Dispatchers.IO).launch {
        log("Coroutine started")
        while (true) {
            val job = launch(Dispatchers.Default) {
                log("UI Coroutine started")
                delay(2.seconds)
                log("UI Coroutine ended")
            }
            if (managerWants) job.join()
            log("Continue coroutine")
        }
    }
}

fun cancelSingleJob() {
    var job1: Job? = null
    var job2: Job? = null
    CoroutineScope(Dispatchers.IO).launch {
        delay(1.seconds)
        log("Parent ...........")
        job1 = launch {
            log("Child 1...........")
            delay(2.seconds)
            log("Child 1 finished")
        }
        job1?.cancel()

        job2 = launch {
            log("Child 2 ...........")
            delay(3.seconds)
            log("Child 2 finished")
        }

        log("Child 1 status ->  isCancelled[${job1?.isCancelled}] isActive[${job1?.isActive}] isCompleted[${job1?.isCompleted}]")
        log("Child 2 status -> isCancelled[${job2?.isCancelled}]  isActive[${job2?.isActive}] isCompleted[${job2?.isCompleted}]")
        log("Parent finished")
    }.invokeOnCompletion {
        log("Parent finished in invokeOnCompletion")
        log("Child 1 status ->  isCancelled[${job1?.isCancelled}] isActive[${job1?.isActive}] isCompleted[${job1?.isCompleted}]")
        log("Child 2 status -> isCancelled[${job2?.isCancelled}]  isActive[${job2?.isActive}] isCompleted[${job2?.isCompleted}]")
    }
}

fun cancelParent() {
    val parentJob = CoroutineScope(Dispatchers.IO).launch {
        delay(1.seconds)
        log("Parent ...........")
        launch {
            log("Child 1...........")
            delay(2.seconds)
            log("Child 1 finished")
        }

        launch {
            log("Child 2 ...........")
            delay(3.seconds)
            log("Child 2 finished")
        }

        log("Parent finished")
    }

    parentJob.invokeOnCompletion {
        log("Parent finished in invokeOnCompletion")
    }

    log("Canceling parent job........")
    parentJob.cancel()
}

fun exceptionIn1Job() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val dispatcher = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        log("Exception was caught by exception handler! for dispatcher[$dispatcher] -> $throwable")
    }

    CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        delay(1.seconds)
        log("Parent ...........")
        launch {
            log("Child 1...........")
            delay(2.seconds)
            throw Exception("Test Exception by Nodrex")
            log("Child 1 finished")
        }

        launch {
            log("Child 2 ...........")
            delay(3.seconds)
            log("Child 2 finished")
            //now let's make loaded data visible
        }

        log("Parent finished")
    }.invokeOnCompletion {
        log("Parent finished in invokeOnCompletion -> $it")
    }
}

fun mySupervisorJob() {
    val exHandler = CoroutineExceptionHandler { _, throwable ->
        log("DefaultCoroutineExceptionHandler -> $throwable")
    }

    val myCoroutineScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Default +
                exHandler
    )

    myCoroutineScope.launch {
        delay(1.seconds)
        log("C1 .....")
        throw Exception("Test Exception by Nodrex")
        log("C1 finished")
    }

    myCoroutineScope.launch {
        delay(2.seconds)
        log("C2 .....")
        log("C2 finished")
    }
}

fun mySupervisorScope() {
    val exHandler = CoroutineExceptionHandler { _, throwable ->
        log("DefaultCoroutineExceptionHandler -> $throwable")
    }

    val myCoroutineScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Default +
                exHandler
    )

    myCoroutineScope.launch {
        delay(1.seconds)
        log("myCoroutineScope .....")
        supervisorScope {
            log("supervisorScope .......")
            launch {
                log("Child 1...........")
                delay(2.seconds)
                throw Exception("Test Exception by Nodrex")
                log("Child 1 finished")
            }

            launch {
                log("Child 2 ...........")
                delay(3.seconds)
                log("Child 2 finished")
            }
            log("supervisorScope finished")
        }
        log("myCoroutineScope finished")
    }.invokeOnCompletion {
        log("myCoroutineScope -> invokeOnCompletion")
    }
}

fun joinAll() {
    val jobs = mutableListOf<Job>()
    (1..5).forEach {
        jobs.add(
            CoroutineScope(Dispatchers.IO).launch {
                delay(1.seconds)
                log("Coroutine finished $it")
            }
        )
    }

    CoroutineScope(Dispatchers.Default).launch {
        log("Observer Coroutine started")
        jobs.joinAll()
    }.invokeOnCompletion {
        log("Observer Coroutine invokeOnCompletion")
    }
}

fun measureDuration() {
    CoroutineScope(Dispatchers.Default).launch {
        log("Coroutine started")
        val duration = measureTimeMillis {
            delay(5.seconds)
        }
        log("Coroutine duration -> $duration")
    }
}

fun coroutineWithTimeout() {
    CoroutineScope(Dispatchers.Default).launch {
        withTimeout(5.seconds) {
            log("Coroutine started")
            while (isActive) {
                log("Coroutine iterating")
                delay(3.seconds)
            }
        }
    }.invokeOnCompletion {
        log("Coroutine invokeOnCompletion")
    }
}

fun coroutineWithTimeoutOrNull() {
    CoroutineScope(Dispatchers.Default).launch {
        val result = withTimeoutOrNull(5.seconds) {
            log("Coroutine started")
            while (isActive) {
                log("Coroutine iterating")
                delay(3.seconds)
                //return@withTimeoutOrNull "Nodrex"
            }
            null
        }
        log("Coroutine result -> $result")
    }.invokeOnCompletion {
        log("Coroutine invokeOnCompletion")
    }
}

fun repeatWork() {
    CoroutineScope(Dispatchers.Default).launch {
        repeat(5) {
            log("Repeating -> $it")
            if (it == 3) return@launch
        }
    }.invokeOnCompletion {
        log("Coroutine invokeOnCompletion")
    }
}

fun asyncAwait() {
    CoroutineScope(Dispatchers.Default).launch {
        val finishedTask1 = myTask("task 1")
        log(finishedTask1.await())

        val finishedTask2 = myTask("task 2")
        log(finishedTask2.await())
        //log(finishedTask1.await())
    }
}

private fun myTask(taskName: String) = CoroutineScope(Dispatchers.Default).async {
    log("async for $taskName started")
    delay(2.seconds)
    return@async "$taskName finished"
}

fun asyncAwaitAll() {
    CoroutineScope(Dispatchers.Default).launch {
        log("Coroutine started")
        val results = listOf(
            myTask("task 1"),
            myTask("task 2")
        ).awaitAll()

        log("results are retrieved")

        results.forEach {
            log(it)
        }
    }.invokeOnCompletion {
        log("Coroutine invokeOnCompletion")
    }
}
