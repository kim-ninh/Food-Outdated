package com.ninh.foodoutdated.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NumberPickerFragment : DialogFragment() {
    var selectedValue: Int = 1
    var selectedIndex: Int = 0

    private val args: NumberPickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (args.endValue - args.startValue + 1 < 1) {
            throw IllegalArgumentException("endValue must >= than startValue")
        }

        val numberSequence = generateSequence(args.startValue) { it + 1 }
            .take(args.endValue - args.startValue + 1)

        val strNumberArr = numberSequence.map { it.toString(10) }
            .toList()
            .toTypedArray()

        selectedIndex = numberSequence.indexOfFirst {
            it == args.selectedValue
        }

        val numberPicker = NumberPicker(requireContext()).apply {
            minValue = 0
            maxValue = strNumberArr.size - 1
            value = selectedIndex
            this.displayedValues = strNumberArr
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                selectedIndex = numberPicker.value
                selectedValue = numberSequence.elementAt(selectedIndex)

                findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                    set(KEY_SELECTED_INDEX, selectedIndex)
                    set(KEY_SELECTED_VALUE, selectedValue)
                }
            }
            .setView(numberPicker)
            .create()
    }

    companion object {
        const val KEY_SELECTED_VALUE = "SELECTED_VALUE"
        const val KEY_SELECTED_INDEX = "SELECTED_INDEX"
    }
}