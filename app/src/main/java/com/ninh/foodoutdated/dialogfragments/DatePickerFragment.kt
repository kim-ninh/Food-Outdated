package com.ninh.foodoutdated.dialogfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninh.foodoutdated.extensions.CalendarExtension
import com.ninh.foodoutdated.extensions.day
import com.ninh.foodoutdated.extensions.month
import com.ninh.foodoutdated.extensions.year
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val args: DatePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(args.pickingDate) {
        DatePickerDialog(
            requireActivity(),
            this@DatePickerFragment, year, month, day
        )
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            val pickedDate = CalendarExtension.getCalendarInstanceFrom(year, month, dayOfMonth)
            set(KEY_PICKED_DATE, pickedDate)
        }
    }

    companion object {
        const val KEY_PICKED_DATE = "PICKED_DATE"
    }
}