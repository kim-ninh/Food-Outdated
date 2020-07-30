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
import com.ninh.foodoutdated.extensions.pop
import java.util.*

class ReminderPickerFragment : DialogFragment(),
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ReminderPickerLayoutBinding

    val expiry: Calendar = Calendar.getInstance()

    private val triggerDateValue: Calendar
        get() = triggerDate.getValueFromExpiry(expiry)

    private var repeatingType = RepeatingType.DAILY

    private val triggerDates = TriggerDate.values()
    var triggerDate: TriggerDate = triggerDates[0]

    private val args: ReminderPickerFragmentArgs by navArgs()
    private var isUserTouchTriggerSpinner = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = ReminderPickerLayoutBinding.inflate(inflater, null, false)

        val argsExpiry: Calendar = args.expiry
        val argsTriggerDateValue: Calendar = args.triggerDate
        val argsRepeatingType: RepeatingType = args.repeatType

        expiry.timeInMillis = argsExpiry.timeInMillis
        triggerDate = TriggerDate.fromExpiryAndTriggerDateValue(expiry, argsTriggerDateValue)
        repeatingType = argsRepeatingType

        with(binding) {
            triggerDateSpinner.adapter = TriggerDateAdapter(triggerDates, argsExpiry)
            repeatTypeSpinner.adapter = RepeatingTypeAdapter()
            triggerDateSpinner.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN){
                    isUserTouchTriggerSpinner = true
                }
                return@setOnTouchListener false
            }

            triggerDateSpinner.onItemSelectedListener = this@ReminderPickerFragment
            repeatTypeSpinner.onItemSelectedListener = this@ReminderPickerFragment

            triggerDateSpinner.setSelection(triggerDate.ordinal)
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

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position)

        if (parent.id == binding.triggerDateSpinner.id) {
            triggerDate = item as TriggerDate
            if (triggerDate == TriggerDate.PICK_A_DATE && isUserTouchTriggerSpinner) {
                val action =
                    ReminderPickerFragmentDirections.actionReminderPickerFragmentToDatePickerFragment(
                        triggerDate.value
                    )

                findNavController().navigate(action)
            }
        }

        if (parent.id == binding.repeatTypeSpinner.id) {
            repeatingType = item as RepeatingType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.reminderPickerFragment)
        val observer = LifecycleEventObserver {_, event ->
            if (event == Lifecycle.Event.ON_RESUME){

                with(navBackStackEntry.savedStateHandle){
                    if (contains(DatePickerFragment.KEY_PICKED_DATE)){
                        val pickedDate = pop<Calendar>(DatePickerFragment.KEY_PICKED_DATE)!!
                        triggerDate.value = pickedDate
                        (binding.triggerDateSpinner.adapter as TriggerDateAdapter).notifyDataSetChanged()
                    }
                }
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)
        lifecycle.addObserver(LifecycleEventObserver{_, event ->
            if (event == Lifecycle.Event.ON_DESTROY){
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        const val KEY_REMIND_INFO = "REMIND_INFO"
    }
}