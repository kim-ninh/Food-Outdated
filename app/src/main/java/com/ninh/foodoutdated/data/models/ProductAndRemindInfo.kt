package com.ninh.foodoutdated.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProductAndRemindInfo(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val remindInfo: RemindInfo
)