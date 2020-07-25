package com.ninh.foodoutdated.mainlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SpacingItemDecoration(val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
//        val lastItemPosition = state.itemCount - 1

        if (position == 0) {
            outRect.top = 2 * spacing
        } else {
            outRect.top = spacing
        }
        outRect.left = spacing
        outRect.right = spacing

//        if (position == lastItemPosition) {
//            outRect.bottom = 2 * spacing
//        }
    }
}