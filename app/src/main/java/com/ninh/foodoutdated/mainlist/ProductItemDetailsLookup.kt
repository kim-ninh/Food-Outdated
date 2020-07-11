package com.ninh.foodoutdated.mainlist

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ProductItemDetailsLookup(private val recyclerView: RecyclerView)
    : ItemDetailsLookup<Long>() {

    private val adapter: ProductAdapter =
        (recyclerView.adapter as ProductAdapter)


    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view: View? = recyclerView.findChildViewUnder(e.x, e.y)

        return view?.let {
            val viewHolder = recyclerView.getChildViewHolder(it)
            if (viewHolder is ProductAdapter.ProductViewHolder){
                val key = adapter.getItemKey(viewHolder.adapterPosition)
                return@let viewHolder.getItemsDetails(key)
            }else{
                null
            }
        }
    }
}