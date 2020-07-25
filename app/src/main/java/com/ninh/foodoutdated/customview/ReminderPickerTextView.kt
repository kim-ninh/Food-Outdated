package com.ninh.foodoutdated.customview

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.models.RepeatingType
import com.ninh.foodoutdated.extensions.isDateEquals
import java.util.*

class ReminderPickerTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), AdapterView.OnItemSelectedListener {

    private val dialogView: View =
        LayoutInflater.from(context).inflate(R.layout.reminder_picker_layout, null)
    private val spinnerTriggerDate: Spinner
    private val spinnerRepeatingType: Spinner
    private val triggerDate: Calendar = Calendar.getInstance()

    init {
        spinnerTriggerDate = dialogView.findViewById(R.id.spinnerTriggerDate)
        spinnerRepeatingType = dialogView.findViewById(R.id.spinnerRepeatType)
    }

    var remindInfo: RemindInfo = RemindInfo()
        set(value) {
            field = value
            updateText(value.triggerDate)

            val deltas = arrayOf(
                Pair(Calendar.DAY_OF_MONTH, -7),
                Pair(Calendar.DAY_OF_MONTH, -15),
                Pair(Calendar.MONTH, -1),
                Pair(Calendar.MONTH, -3)
            )

            val datesBeforeExp = Array(deltas.size) { index ->
                (expiryDate.clone() as Calendar).apply {
                    add(deltas[index].first, deltas[index].second)
                }
            }

            datesBeforeExp.forEachIndexed { index, calendar ->
                if (value.triggerDate.isDateEquals(calendar)) {
                    spinnerTriggerDate.setSelection(index)
                }
            }

            enumValues<RepeatingType>().forEachIndexed { index, repeatingType ->
                if (value.repeating == repeatingType) {
                    spinnerRepeatingType.setSelection(index)
                }
            }

        }

    private val _expiryDate: Calendar = Calendar.getInstance()
    var expiryDate: Calendar
        set(value) {
            _expiryDate.timeInMillis = value.timeInMillis
            updateTriggerDate(spinnerTriggerDate.selectedItem.toString())
        }
        get() {
            return _expiryDate.clone() as Calendar
        }

    private val reminderPickerDialog by lazy {
        MaterialAlertDialogBuilder(context)
            .setTitle("Edit reminder")
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                remindInfo.triggerDate.timeInMillis = triggerDate.timeInMillis
                remindInfo.triggerDate.set(Calendar.HOUR, 8)
                remindInfo.triggerDate.set(Calendar.MINUTE, 0)
                remindInfo.triggerDate.set(Calendar.SECOND, 0)

            }
            .setNegativeButton(resources.getString(android.R.string.cancel)) { _, _ ->

            }
            .setView(dialogView)
            .create()
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        spinnerTriggerDate.onItemSelectedListener = this
        spinnerRepeatingType.onItemSelectedListener = this
        updateTriggerDate(spinnerTriggerDate.selectedItem.toString())
    }

    override fun performClick(): Boolean {
        reminderPickerDialog.show()
        return super.performClick()
    }

    private fun updateText(calendar: Calendar) {
        this.text = DateFormat.format(resources.getString(R.string.reminder_date_pattern), calendar)
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position).toString()
        if (parent.id == R.id.spinnerTriggerDate) {
            updateTriggerDate(item)
        }

        if (parent.id == R.id.spinnerRepeatType) {
            remindInfo.repeating = with(resources) {
                when (item) {
                    getString(R.string.does_not_repeat) -> RepeatingType.NO_REPEAT
                    getString(R.string.daily) -> RepeatingType.DAILY
                    getString(R.string.weekly) -> RepeatingType.WEEKLY
                    getString(R.string.monthly) -> RepeatingType.MONTHLY
                    else -> throw RuntimeException("Repeat type not found")
                }
            }
        }
    }

    private fun updateTriggerDate(timeBeforeExpiry: String) {
        triggerDate.timeInMillis = expiryDate.timeInMillis
        with(resources){
            when(timeBeforeExpiry){
                getString(R.string.a_week) -> triggerDate.add(Calendar.DAY_OF_MONTH, -7)
                getString(R.string.fifteen_date) -> triggerDate.add(Calendar.DAY_OF_MONTH, -15)
                getString(R.string.a_month) -> triggerDate.add(Calendar.MONTH, -1)
                getString(R.string.three_month) -> triggerDate.add(Calendar.MONTH, -3)
                else -> throw RuntimeException("Trigger date not valid")
            }
        }
        updateText(triggerDate)
    }
}