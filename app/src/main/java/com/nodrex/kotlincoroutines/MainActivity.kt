package com.nodrex.kotlincoroutines

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nodrex.kotlincoroutines.coroutines.asyncAwait
import com.nodrex.kotlincoroutines.coroutines.asyncAwaitAll
import com.nodrex.kotlincoroutines.coroutines.cancelParent
import com.nodrex.kotlincoroutines.coroutines.cancelSingleJob
import com.nodrex.kotlincoroutines.coroutines.coroutineCancellation
import com.nodrex.kotlincoroutines.coroutines.coroutineCancellationWithEnsureActive
import com.nodrex.kotlincoroutines.coroutines.coroutineCancellationWithoutDirectCheck
import com.nodrex.kotlincoroutines.coroutines.coroutineCompletionDetection
import com.nodrex.kotlincoroutines.coroutines.coroutineExceptionHandler
import com.nodrex.kotlincoroutines.coroutines.coroutineWithTimeout
import com.nodrex.kotlincoroutines.coroutines.coroutineWithTimeoutOrNull
import com.nodrex.kotlincoroutines.coroutines.exceptionIn1Job
import com.nodrex.kotlincoroutines.coroutines.joinAll
import com.nodrex.kotlincoroutines.coroutines.joinCoroutine
import com.nodrex.kotlincoroutines.coroutines.measureDuration
import com.nodrex.kotlincoroutines.coroutines.mySupervisorScope
import com.nodrex.kotlincoroutines.coroutines.repeatWork
import com.nodrex.kotlincoroutines.ui.theme.KotlinCoroutinesTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.ContinuationInterceptor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinCoroutinesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")

                    //myThreadPool()
                    //limitedDispatcher()
                    //stressTest()
                    //contextSwitch()
                    //contextSwitchOptimization()
                    //coroutineCancellation()
                    //coroutineCancellationWithoutDirectCheck()
                    //coroutineCancellationWithEnsureActive()
                    //coroutineCompletionDetection()
                    //coroutineExceptionHandler()
                    //joinCoroutine()

                    //cancelSingleJob()
                    //cancelParent()
                    //exceptionIn1Job()
                    //mySupervisorJob()
                    //mySupervisorScope()
                    //joinAll()
                    //measureDuration()
                    //coroutineWithTimeout()
                    //coroutineWithTimeoutOrNull()
                    //repeatWork()
                    //asyncAwait()
                    asyncAwaitAll()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinCoroutinesTheme {
        Greeting("Android")
    }
}

fun log(text: String) {
    Log.d("Kotlin_Coroutine_Tag", text)
}