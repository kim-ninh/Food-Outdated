package com.ninh.foodoutdated.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.models.RepeatingType
import com.ninh.foodoutdated.databinding.ReminderPickerLayoutBinding
import com.ninh.foodoutdated.extensions.isDateEquals
import java.util.*

class ReminderPickerFragment : DialogFragment(),
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ReminderPickerLayoutBinding

    val expiry: Calendar = Calendar.getInstance()

    private var triggerDateItem = ""
    private val _triggerDate: Calendar = Calendar.getInstance()
    private val triggerDate: Calendar
        get() = stringResToTriggerDate(triggerDateItem, expiry)


    var triggerTime: Calendar? = Calendar.getInstance()


    private var repeatTypeItem = ""
    private val repeatingType: RepeatingType
        get() = stringResToRepeatType[repeatTypeItem] ?: error("Repeat type item not valid")

    private val args: ReminderPickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = ReminderPickerLayoutBinding.inflate(inflater)

        triggerDateItem = getString(R.string.default_trigger_date)
        repeatTypeItem = getString(R.string.daily)
        val argsExpiry: Calendar = args.expiry
        val argsTriggerDate: Calendar? = args.triggerDate
        val argsTriggerTime: Calendar? = args.triggerTime
        val argsRepeatingType: RepeatingType = args.repeatType

        expiry.timeInMillis = argsExpiry.timeInMillis

        with(binding) {
            triggerDateSpinner.onItemSelectedListener = this@ReminderPickerFragment
            repeatTypeSpinner.onItemSelectedListener = this@ReminderPickerFragment

            resources.getStringArray(R.array.spinner_repeating_type)
                .indexOfFirst {
                    it == repeatTypeToStringRes[argsRepeatingType]
                }.also {
                    repeatTypeSpinner.setSelection(it)
                }


            resources.getStringArray(R.array.spinner_trigger_date)
                .indexOfFirst {
                    return@indexOfFirst if (argsTriggerDate != null) {
                        argsTriggerDate.isDateEquals(stringResToTriggerDate(it, expiry))
                    } else {
                        it == triggerDateItem
                    }
                }.let {
                    triggerDateSpinner.setSelection(it)
                }
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Edit reminder")
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                    val remindInfo = RemindInfo(triggerDate, repeatingType)
                    set(KEY_REMIND_INFO, remindInfo)
                }
            }
            .setNegativeButton(resources.getString(android.R.string.cancel)) { _, _ ->

            }
            .setView(binding.root)
            .create()
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position).toString()

        if (parent.id == binding.triggerDateSpinner.id) {
            triggerDateItem = item
        }

        if (parent.id == binding.repeatTypeSpinner.id) {
            repeatTypeItem = item
        }
    }

    private val repeatTypeToStringRes by lazy {
        mapOf(
            RepeatingType.NO_REPEAT to getString(R.string.does_not_repeat),
            RepeatingType.DAILY to getString(R.string.daily),
            RepeatingType.WEEKLY to getString(R.string.weekly),
            RepeatingType.MONTHLY to getString(R.string.monthly)
        )
    }

    private val stringResToRepeatType by lazy {
        mapOf(
            getString(R.string.does_not_repeat) to RepeatingType.NO_REPEAT,
            getString(R.string.daily) to RepeatingType.DAILY,
            getString(R.string.weekly) to RepeatingType.WEEKLY,
            getString(R.string.monthly) to RepeatingType.MONTHLY
        )
    }

    private fun stringResToTriggerDate(durationBeforeExpiry: String, expiry: Calendar) =
        _triggerDate.apply {
            timeInMillis = expiry.timeInMillis
            when (durationBeforeExpiry) {
                getString(R.string.a_week) -> add(Calendar.DAY_OF_MONTH, -7)
                getString(R.string.fifteen_date) -> add(Calendar.DAY_OF_MONTH, -15)
                getString(R.string.a_month) -> add(Calendar.MONTH, -1)
                getString(R.string.three_month) -> add(Calendar.MONTH, -3)
            }
        }

    companion object {
        const val KEY_REMIND_INFO = "REMIND_INFO"
    }
}