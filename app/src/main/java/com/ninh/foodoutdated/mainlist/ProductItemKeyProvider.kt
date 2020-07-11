package com.ninh.foodoutdated.mainlist

import androidx.recyclerview.selection.ItemKeyProvider
import com.ninh.foodoutdated.mainlist.ProductAdapter

class ProductItemKeyProvider(private val adapter: ProductAdapter)
    : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long {
        return adapter.getItemKey(position)
    }

    override fun getPosition(key: Long): Int {
        return adapter.getPosition(key)
    }
}