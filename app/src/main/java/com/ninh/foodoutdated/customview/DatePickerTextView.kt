package com.ninh.foodoutdated.customview

import android.app.DatePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatTextView
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.extensions.day
import com.ninh.foodoutdated.extensions.month
import com.ninh.foodoutdated.extensions.year
import java.util.*


class DatePickerTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr),
    DatePickerDialog.OnDateSetListener {

    var onDatePickChanged: ((Calendar) -> Unit)? = null

    var datePicked: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            datePickerDialog.updateDate(value.year, value.month, value.day)
            updateText(value)
            onDatePickChanged?.invoke(field.clone() as Calendar)
        }

    private val datePickerDialog by lazy {
        val today = Calendar.getInstance()
        DatePickerDialog(
            context, this,
            today.year, today.month, today.day
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updateText()
    }

    override fun performClick(): Boolean {
        datePickerDialog.show()
        return super.performClick()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        datePicked.set(year, month, dayOfMonth)
        updateText()
        onDatePickChanged?.invoke(datePicked.clone() as Calendar)
    }

    private fun updateText(){
        updateText(datePicked)
    }

    private fun updateText(calendar: Calendar) {
        this.text = DateFormat.format(resources.getString(R.string.date_pattern_vn), calendar)
    }
}