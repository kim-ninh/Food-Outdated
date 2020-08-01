package com.ninh.foodoutdated.dialogfragments.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.databinding.TriggerTimeItemBinding
import com.ninh.foodoutdated.databinding.TriggerTimeItemDropDownBinding
import com.ninh.foodoutdated.dialogfragments.TriggerTime

class TriggerTimeAdapter(
    private val triggerTimes: Array<TriggerTime>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.trigger_time_item, parent, false)

        val binding = TriggerTimeItemBinding.bind(_convertView)
        val triggerTimeValue = triggerTimes[position].value
        binding.triggerTimeValue.text = DateFormat.format(
            parent.resources.getString(R.string.standard_time_format),
            triggerTimeValue
        )
        return _convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.trigger_time_item_drop_down, parent, false)

        val resources = parent.resources
        val binding = TriggerTimeItemDropDownBinding.bind(_convertView)
        val triggerTimeValue = triggerTimes[position].value

        with(resources) {
            binding.triggerTimeLabel.text = getString(triggerTimes[position].toStringRes())

            if (position != TriggerTime.PICK_A_TIME.ordinal) {
                binding.triggerTimeValue.text =
                    DateFormat.format(getString(R.string.standard_time_format), triggerTimeValue)
            }
        }

        return _convertView
    }

    override fun getItem(position: Int): Any =
        triggerTimes[position]

    override fun getItemId(position: Int): Long =
        triggerTimes[position].ordinal.toLong()

    override fun getCount(): Int = triggerTimes.size
}