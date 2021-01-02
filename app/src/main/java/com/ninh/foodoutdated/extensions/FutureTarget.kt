package com.ninh.foodoutdated.extensions

import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun <T> FutureTarget<T>.await(): T = withContext(Dispatchers.Default){
    get()
}