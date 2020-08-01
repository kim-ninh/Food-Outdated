package com.ninh.foodoutdated.dialogfragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.models.RepeatingType
import com.ninh.foodoutdated.databinding.ReminderPickerLayoutBinding

import com.ninh.foodoutdated.dialogfragments.adapters.RepeatingTypeAdapter
import com.ninh.foodoutdated.dialogfragments.adapters.TriggerDateAdapter
import com.ninh.foodoutdated.dialogfragments.adapters.TriggerTimeAdapter
import com.ninh.foodoutdated.extensions.hour
import com.ninh.foodoutdated.extensions.minute
import com.ninh.foodoutdated.extensions.pop
import java.util.*

class ReminderPickerFragment : DialogFragment(),
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ReminderPickerLayoutBinding

    val expiry: Calendar = Calendar.getInstance()

    private val triggerDateValue: Calendar
        get() = triggerDate.getValueFromExpiry(expiry).apply {
            set(Calendar.HOUR_OF_DAY, triggerTime.value.hour)
            set(Calendar.MINUTE, triggerTime.value.minute)
        }

    private var repeatingType = RepeatingType.DAILY

    private val triggerDates = TriggerDate.values()
    var triggerDate: TriggerDate = triggerDates[0]

    private val triggerTimes = TriggerTime.constValues
    var triggerTime: TriggerTime = triggerTimes[0]

    private val args: ReminderPickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = ReminderPickerLayoutBinding.inflate(inflater, null, false)

        val argsExpiry: Calendar = args.expiry
        val argsTriggerDateValue: Calendar = args.triggerDate
        val argsRepeatingType: RepeatingType = args.repeatType
        val timeInMillis = argsTriggerDateValue.timeInMillis

        expiry.timeInMillis = argsExpiry.timeInMillis
        triggerDate = TriggerDate.fromExpiryAndTriggerDateValue(expiry, timeInMillis)
        triggerTime = TriggerTime.fromTriggerTimeValue(timeInMillis)
        repeatingType = argsRepeatingType

        with(binding) {
            triggerDateSpinner.adapter = TriggerDateAdapter(triggerDates, argsExpiry)
            triggerTimeSpinner.adapter = TriggerTimeAdapter(triggerTimes)
            repeatTypeSpinner.adapter = RepeatingTypeAdapter()

            triggerDateSpinner.onItemSelectedListener = this@ReminderPickerFragment
            triggerTimeSpinner.onItemSelectedListener = this@ReminderPickerFragment
            repeatTypeSpinner.onItemSelectedListener = this@ReminderPickerFragment

            triggerDateSpinner.setSelection(triggerDate.ordinal)
            triggerTimeSpinner.setSelection(triggerTime.ordinal)
            repeatTypeSpinner.setSelection(repeatingType.ordinal)
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Edit reminder")
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                    val remindInfo = RemindInfo(triggerDateValue, repeatingType)
                    set(KEY_REMIND_INFO, remindInfo)
                }
            }
            .setNegativeButton(resources.getString(android.R.string.cancel)) { _, _ ->

            }
            .setView(binding.root)
            .create()
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) =
        with(binding) {
            val item = parent.getItemAtPosition(position)

            if (parent.id == triggerDateSpinner.id) {
                triggerDate = item as TriggerDate
                if (triggerDate == TriggerDate.PICK_A_DATE && triggerDateSpinner.isSelectionFromUser) {
                    val action =
                        ReminderPickerFragmentDirections.actionReminderPickerFragmentToDatePickerFragment(
                            triggerDate.value
                        )

                    findNavController().navigate(action)
                }
            }

            if (parent.id == triggerTimeSpinner.id){
                triggerTime = item as TriggerTime
                if (triggerTime == TriggerTime.PICK_A_TIME && triggerTimeSpinner.isSelectionFromUser){
                    val action =
                        ReminderPickerFragmentDirections.actionReminderPickerFragmentToTimePickerFragment(
                            triggerTime.value
                        )

                    findNavController().navigate(action)
                }
            }

            if (parent.id == repeatTypeSpinner.id) {
                repeatingType = item as RepeatingType
            }
            Unit
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.reminderPickerFragment)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                with(navBackStackEntry.savedStateHandle) {
                    if (contains(DatePickerFragment.KEY_PICKED_DATE)) {
                        val pickedDate = pop<Calendar>(DatePickerFragment.KEY_PICKED_DATE)!!
                        triggerDate.value = pickedDate
                        (binding.triggerDateSpinner.adapter as TriggerDateAdapter).notifyDataSetChanged()
                    }

                    if (contains(TimePickerFragment.KEY_PICKED_TIME)){
                        val pickedTime = pop<Calendar>(TimePickerFragment.KEY_PICKED_TIME)!!
                        triggerTime.value.timeInMillis = pickedTime.timeInMillis
                        (binding.triggerTimeSpinner.adapter as TriggerTimeAdapter).notifyDataSetChanged()
                    }
                }
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        const val KEY_REMIND_INFO = "REMIND_INFO"
    }
}