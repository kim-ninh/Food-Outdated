package com.ninh.foodoutdated.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NumberPickerFragment : DialogFragment() {

    private val args: NumberPickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        require(args.endValue - args.startValue + 1 < 1) { "endValue must >= startValue" }

        val numberPicker = NumberPicker(requireContext()).apply {
            minValue = args.startValue
            maxValue = args.endValue
            value = args.selectedValue
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                    set(KEY_SELECTED_VALUE, numberPicker.value)
                }
            }
            .setView(numberPicker)
            .create()
    }

    companion object {
        const val KEY_SELECTED_VALUE = "SELECTED_VALUE"
    }
}