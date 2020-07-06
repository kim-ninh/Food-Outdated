package com.ninh.foodoutdated

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ninh.foodoutdated.MyAdapter.MyViewHolder
import com.ninh.foodoutdated.Product.EXPIRY_STATE
import com.orhanobut.logger.Logger
import java.util.*

class MyAdapter(private val mDataset: List<Product>) :
    RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var tracker: SelectionTracker<Long>
    fun setTracker(tracker: SelectionTracker<Long>) {
        this.tracker = tracker
    }

    fun setContext(context: Context?) {
        Companion.context = context
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val product = mDataset[position]
        holder.bind(product, tracker.isSelected(product.id))
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {

        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_item, parent, false) as LinearLayout
        //...
        return MyViewHolder(v)
    }

    override fun getItemId(position: Int): Long {
        return mDataset[position].id
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataset.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {
        val itemDetails: ItemDetails<Long>
            get() = object : ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long? {
                    return itemId
                }
            }

        fun bind(product: Product, isActive: Boolean) {
            val txtName = linearLayout.findViewById<TextView>(R.id.product_name)
            val txtEpiry = linearLayout.findViewById<TextView>(R.id.product_expiry)
            val imageThumbnail = linearLayout.findViewById<ImageView>(R.id.product_thumbnail)
            txtName.text = product.name
            txtEpiry.text = DateFormat.format(Utils.Companion.DATE_PATTERN_VN, product.expiry)
            Glide.with(context!!)
                    .load(product.thumbnail)
                    .into(imageThumbnail)
            Logger.i("image path: %s", product.thumbnail)
            linearLayout.isActivated = isActive
            val greenColor = context!!.resources.getColor(R.color.green)
            val redColor = context!!.resources.getColor(R.color.red)
            val yellowColor = context!!.resources.getColor(R.color.yellow)
            when (product.state) {
                EXPIRY_STATE.NEW -> txtEpiry.setTextColor(greenColor)
                EXPIRY_STATE.EXPIRED -> txtEpiry.setTextColor(redColor)
                EXPIRY_STATE.NEARLY_EXPIRY -> txtEpiry.setTextColor(yellowColor)
            }
        }

    }

    companion object {
        private var context: Context? = null
        private val random = Random()
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    init {
        setHasStableIds(true)
    }
}