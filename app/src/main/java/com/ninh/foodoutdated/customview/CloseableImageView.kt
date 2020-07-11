package com.ninh.foodoutdated.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.ninh.foodoutdated.R

typealias OnCloseListener = () -> Unit

class CloseableImageView

@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private val imageView: ImageView
    private val imageViewDelete: ImageView

    val internalImageView: ImageView
        get() {
            imageViewDelete.visibility = View.VISIBLE
            return imageView
        }

    private var onCloseListener: OnCloseListener? = null

    init {
        View.inflate(context, R.layout.closeable_image_view, this)
        imageView = rootView.findViewById(R.id.imageView)
        imageViewDelete = rootView.findViewById(R.id.imageViewDelete)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        imageViewDelete.setOnClickListener{
            imageView.setImageDrawable(null)
            imageViewDelete.visibility = View.INVISIBLE
            onCloseListener?.invoke()
        }
    }

    fun setOnCloseListener(onCloseListener: OnCloseListener){
        this.onCloseListener = onCloseListener
    }
}
