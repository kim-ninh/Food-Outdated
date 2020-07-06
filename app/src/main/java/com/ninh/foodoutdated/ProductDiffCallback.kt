package com.ninh.foodoutdated

import androidx.recyclerview.widget.DiffUtil
import com.ninh.foodoutdated.models.Product

class ProductDiffCallback(
    private val oldProducts: List<Product>,
    private val newProducts: List<Product>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean =
        oldProducts[oldItemPosition].id == newProducts[newItemPosition].id

    override fun getOldListSize(): Int = oldProducts.size

    override fun getNewListSize(): Int = newProducts.size

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int): Boolean =
        oldProducts[oldItemPosition] == newProducts[newItemPosition]
}