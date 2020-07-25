package com.ninh.foodoutdated.mainlist

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.data.models.ExpiryState
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.databinding.ProductItemBinding

class ProductAdapter(
    private val onItemClickListener: ((Int) -> Unit)? = null
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(
    PRODUCT_DIFF_CALLBACK
) {

    lateinit var tracker: SelectionTracker<Long>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, tracker.isSelected(product.id.toLong()), onItemClickListener)
    }

    fun getItemKey(position: Int): Long = getItem(position).id.toLong()

    fun getPosition(key: Long): Int {
        var position = currentList.indexOfFirst { it.id.toLong() == key }
        if (position == -1) {
            position = RecyclerView.NO_POSITION
        }
        return position
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding: ProductItemBinding = ProductItemBinding.bind(itemView)

        private val GREEN_COLOR by lazy { ContextCompat.getColor(itemView.context, R.color.green) }
        private val RED_COLOR by lazy { ContextCompat.getColor(itemView.context, R.color.red) }
        private val YELLOW_COLOR by lazy { ContextCompat.getColor(itemView.context, R.color.yellow) }

        fun bind(product: Product, isActive: Boolean, onItemClickListener: ((Int) -> Unit)?) {
            with(binding) {
                itemView.isActivated = isActive
                name.text = product.name
                expiry.text = DateFormat.format(
                    itemView.resources.getString(R.string.date_pattern_vn),
                    product.expiry
                )
                when (product.state) {
                    ExpiryState.NEW -> expiry.setTextColor(GREEN_COLOR)
                    ExpiryState.EXPIRED -> expiry.setTextColor(RED_COLOR)
                    ExpiryState.NEARLY_EXPIRY -> expiry.setTextColor(YELLOW_COLOR)
                }

                Glide.with(thumbnail)
                    .load(product.thumb)
                    .fallback(R.drawable.ic_waste)
                    .into(thumbnail)

                onItemClickListener?.let { listener ->
                    itemView.setOnClickListener {
                        listener.invoke(product.id)
                    }
                }
            }
        }

        fun getItemsDetails(key: Long): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? {
                    return key
                }

                override fun getPosition(): Int {
                    return adapterPosition
                }

            }
        }
    }
}

object PRODUCT_DIFF_CALLBACK : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}