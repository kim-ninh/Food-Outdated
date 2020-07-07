package com.ninh.foodoutdated

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.models.Product

class ProductAdapter(
    private var products: List<Product> = ArrayList()
)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateList(products: List<Product>){
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(this.products, products))
        this.products = products
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], false)
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

        fun bind(product: Product, isActive: Boolean){
            productTextView.text = product.name
            productExpiryTextView.text = DateFormat.format(Utils.DATE_PATTERN_VN, product.expiry)
            when(product.state){
                ExpiryState.NEW -> productExpiryTextView.setTextColor(GREEN_COLOR)
                ExpiryState.EXPIRED -> productExpiryTextView.setTextColor(RED_COLOR)
                ExpiryState.NEARLY_EXPIRY -> productExpiryTextView.setTextColor(YELLOW_COLOR)
            }

            Glide.with(productImageView)
                .load(product.uri)
                .into(productImageView)
        }
    }
}