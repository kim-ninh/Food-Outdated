package com.ninh.foodoutdated.dialogfragments.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.databinding.TriggerDateItemBinding
import com.ninh.foodoutdated.databinding.TriggerDateItemDropDownBinding


import com.ninh.foodoutdated.dialogfragments.TriggerDate
import com.orhanobut.logger.Logger
import java.util.*

class TriggerDateAdapter(
    private val triggerDates: Array<TriggerDate>,
    private val expiry: Calendar) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.trigger_date_item, parent, false)

        val binding = TriggerDateItemBinding.bind(_convertView)
        val triggerDateValue = triggerDates[position].getValueFromExpiry(expiry)
        binding.triggerDateValue.text = DateFormat.format(
            _convertView.resources.getString(R.string.reminder_date_pattern),
            triggerDateValue
        )

        return _convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.trigger_date_item_drop_down, parent, false)

        val resources = parent.resources

        val binding = TriggerDateItemDropDownBinding.bind(_convertView)
        val triggerDateValue =
            triggerDates[position].getValueFromExpiry(expiry)

        with(resources) {
            binding.triggerDateLabel.text = getString(triggerDates[position].toStringRes())

            if (position != TriggerDate.PICK_A_DATE.ordinal) {
                binding.triggerDateValue.text =
                    DateFormat.format(getString(R.string.reminder_date_pattern), triggerDateValue)
            }
        }

        return _convertView
    }

    override fun getItem(position: Int): Any =
        triggerDates[position]

    override fun getItemId(position: Int): Long =
        triggerDates[position].ordinal.toLong()

    override fun getCount(): Int =
        triggerDates.size
}