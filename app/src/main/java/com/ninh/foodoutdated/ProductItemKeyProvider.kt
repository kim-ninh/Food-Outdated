package com.ninh.foodoutdated

import androidx.recyclerview.selection.ItemKeyProvider

class ProductItemKeyProvider(private val adapter: ProductAdapter)
    : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long {
        return adapter.getItemKey(position)
    }

    override fun getPosition(key: Long): Int {
        return adapter.getPosition(key)
    }
}