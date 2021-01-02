package com.ninh.foodoutdated.extensions

import android.content.res.Resources
import androidx.annotation.AnyRes

fun Resources.getResourceNameOrNull(@AnyRes resId: Int): String? = try {
    getResourceName(resId)
} catch (e: Resources.NotFoundException) {
    null
}