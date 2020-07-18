package com.ninh.foodoutdated.editproduct

import androidx.annotation.DrawableRes

data class ProductImageAction(
    val actionName: String,
    @DrawableRes val resId: Int,
    val action: () -> Unit = {}
)