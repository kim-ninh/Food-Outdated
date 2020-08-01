package com.ninh.foodoutdated.customview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner


class NSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatSpinner(context, attrs), View.OnTouchListener {

    var isSelectionFromUser = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        setOnTouchListener(this)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        val sameSelected = position == selectedItemPosition
        super.setSelection(position, animate)
        if (sameSelected) {
            onItemSelectedListener
                ?.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun setSelection(position: Int) {
        val sameSelected = position == selectedItemPosition
        super.setSelection(position)
        if (sameSelected) {
            onItemSelectedListener
                ?.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN){
            isSelectionFromUser = true
        }
        return false
    }
}