package com.ninh.foodoutdated.extensions

import androidx.lifecycle.SavedStateHandle

fun<T> SavedStateHandle.pop(key: String): T?{
    val item = get<T>(key)
    remove<T>(key)

    return item
}