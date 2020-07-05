package com.ninh.foodoutdated

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class MyItemKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(i: Int): Long {
        return recyclerView.adapter!!.getItemId(i)
    }

    override fun getPosition(aLong: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(aLong)
                ?: return RecyclerView.NO_POSITION
        return viewHolder.layoutPosition
    }

}