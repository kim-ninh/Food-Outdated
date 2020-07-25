package com.ninh.foodoutdated.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NumberPickerTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var displayedValues = Array(0) { "" }
        set(value) {
            if (value.isNotEmpty()){
                this.text = value[0]
            }
            field = value
        }

    var currentValue = 0
        set(value) {
            numberPicker.value = value
            updateText()
            field = value
        }

    private val numberPicker by lazy {
        NumberPicker(this.context).apply {
            if (this@NumberPickerTextView.displayedValues.isNotEmpty()) {
                minValue = 0
                maxValue = this@NumberPickerTextView.displayedValues.size - 1
                value = 0
                this.displayedValues = this@NumberPickerTextView.displayedValues
            }
        }
    }

    private val numberPickerDialog by lazy {
        MaterialAlertDialogBuilder(context)
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                updateText()
                currentValue = numberPicker.value
            }
            .setView(numberPicker)
            .create()
    }

    private fun updateText() {
        this.text = displayedValues[numberPicker.value]
    }

    override fun performClick(): Boolean {
        numberPickerDialog.show()
        return super.performClick()
    }
}