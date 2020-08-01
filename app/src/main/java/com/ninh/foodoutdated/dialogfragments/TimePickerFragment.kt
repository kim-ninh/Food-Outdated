package com.ninh.foodoutdated.dialogfragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninh.foodoutdated.extensions.CalendarUtils
import com.ninh.foodoutdated.extensions.hour
import com.ninh.foodoutdated.extensions.minute

class TimePickerFragment : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(args.pickingTime) {
        TimePickerDialog(
            requireActivity(),
            this@TimePickerFragment,
            hour, minute, DateFormat.is24HourFormat(requireActivity())
        )
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            val pickedTime = CalendarUtils.getCalendarFrom(hourOfDay, minute)
            set(KEY_PICKED_TIME, pickedTime)
        }
    }

    companion object {
        const val KEY_PICKED_TIME = "PICKED_TIME"
    }

}