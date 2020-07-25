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

    private val _datePicked: Calendar = Calendar.getInstance()
    var datePicked: Calendar
        set(value) {
            _datePicked.timeInMillis = value.timeInMillis
            datePickerDialog.updateDate(_datePicked.year, _datePicked.month, _datePicked.day)
            updateText()
            onDatePickChanged?.invoke(_datePicked.clone() as Calendar)
        }
        get() {
            return _datePicked.clone() as Calendar
        }

    private val datePickerDialog by lazy {
        DatePickerDialog(
            context, this,
            _datePicked.year, _datePicked.month, _datePicked.day
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
        _datePicked.set(year, month, dayOfMonth)
        updateText()
        onDatePickChanged?.invoke(_datePicked.clone() as Calendar)
    }

    private fun updateText() {
        this.text = DateFormat.format(resources.getString(R.string.date_pattern_vn), _datePicked)
    }
}