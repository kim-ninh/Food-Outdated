package com.ninh.foodoutdated.custom.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.ninh.foodoutdated.R

class CloseableImageView

@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private lateinit var imageView: ImageView
    private lateinit var imageViewDelete: ImageView

    init {
        initView()
    }

    private fun initView() {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.image_holder_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        imageView = findViewById(R.id.imageView)
        imageView.tag = ""
        imageViewDelete = findViewById(R.id.imageViewDelete)
        imageViewDelete.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === imageViewDelete) {
            this.visibility = View.GONE
        }
    }

    fun setImageUri(uri: Uri) {
        this.visibility = View.VISIBLE
        imageView.setImageURI(uri)
        imageView.tag = uri.toString()
    }

    val imageUri: String
        get() = imageView.tag.toString()
}