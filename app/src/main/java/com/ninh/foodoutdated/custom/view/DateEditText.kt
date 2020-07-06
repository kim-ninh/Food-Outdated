package com.ninh.foodoutdated.custom.view

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatEditText
import com.ninh.foodoutdated.Utils
import java.util.*

class DateEditText
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatEditText(context, attrs, defStyleAttr), OnDateSetListener {

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c[year, month] = dayOfMonth
        this.setText(DateFormat.format(Utils.DATE_PATTERN_VN, c))
    }

    override fun performClick(): Boolean {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this.context, this, year, month, day
        )

        datePickerDialog.show()
        return super.performClick()
    }
}