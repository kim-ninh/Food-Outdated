package com.ninh.foodoutdated

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.ninh.foodoutdated.MyAdapter.MyViewHolder

internal class MyDetailsLookup(private val mRecyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(motionEvent: MotionEvent): ItemDetails<Long>? {
        val view = mRecyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
        if (view != null) {
            val holder = mRecyclerView.getChildViewHolder(view)
            if (holder is MyViewHolder) {
                return holder.itemDetails
            }
        }
        return null
    }

}