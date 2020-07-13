package com.ninh.foodoutdated.newproduct

import androidx.annotation.DrawableRes

data class ProductImageAction(
    val actionName: String,
    @DrawableRes val resId: Int,
    val action: () -> Unit = {}
)