package com.ninh.foodoutdated.editproduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.R

class ProductImageActionAdapter(
    private val productImageActions: List<ProductImageAction>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent!!.context)
                .inflate(R.layout.product_image_action_item, parent, false)

        val imageView: ImageView = _convertView.findViewById(R.id.product_image_action_image_view)
        val textView: TextView = _convertView.findViewById(R.id.product_image_action_text_view)

        textView.text = getItem(position).actionName
        Glide.with(imageView)
            .load(getItem(position).resId)
            .into(imageView)

        return _convertView
    }

    override fun getItem(position: Int): ProductImageAction = productImageActions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int =  productImageActions.size

}