package com.ninh.foodoutdated.mainlist

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.data.models.ExpiryState
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.Product

class ProductAdapter(
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
        holder.bind(product, tracker.isSelected(product.id))
    }

    fun getItemKey(position: Int): Long = getItem(position).id!!

    fun getPosition(key: Long): Int {
        var position = currentList.indexOfFirst { it.id == key }
        if (position == -1) {
            position = RecyclerView.NO_POSITION
        }
        return position
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImageView: ImageView = itemView.findViewById(R.id.product_thumbnail)
        private val productTextView: TextView = itemView.findViewById(R.id.product_name)
        private val productExpiryTextView: TextView = itemView.findViewById(R.id.product_expiry)

        private val GREEN_COLOR by lazy {
            val color = itemView.resources.getColor(R.color.green)
            color
        }

        private val RED_COLOR by lazy {
            val color = itemView.resources.getColor(R.color.red)
            color
        }

        private val YELLOW_COLOR by lazy {
            val color = itemView.resources.getColor(R.color.yellow)
            color
        }

        fun bind(product: Product, isActive: Boolean) {
            itemView.isActivated = isActive
            productTextView.text = product.name
            productExpiryTextView.text = DateFormat.format(itemView.resources.getString(R.string.date_pattern_vn), product.expiry)
            when (product.state) {
                ExpiryState.NEW -> productExpiryTextView.setTextColor(GREEN_COLOR)
                ExpiryState.EXPIRED -> productExpiryTextView.setTextColor(RED_COLOR)
                ExpiryState.NEARLY_EXPIRY -> productExpiryTextView.setTextColor(YELLOW_COLOR)
            }

            Glide.with(productImageView)
                .load(product.file)
                .into(productImageView)
        }

        fun getItemsDetails(key: Long): ItemDetailsLookup.ItemDetails<Long>{
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

object PRODUCT_DIFF_CALLBACK : DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean
            = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean
            = oldItem == newItem
}