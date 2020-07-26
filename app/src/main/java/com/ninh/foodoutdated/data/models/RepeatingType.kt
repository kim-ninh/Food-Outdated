package com.ninh.foodoutdated.data.models

enum class RepeatingType {
    NO_REPEAT, DAILY, WEEKLY, MONTHLY;

    val toTimeDurationInMillis: Long
        get() = when (this) {
            NO_REPEAT -> 0
            DAILY -> 1000 * 60 * 60 * 24
            WEEKLY -> 1000 * 60 * 60 * 24 * 7
            MONTHLY -> 1000 * 60 * 60 * 24 * 31L
        }
}