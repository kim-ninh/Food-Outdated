package com.ninh.foodoutdated.dialogfragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.RepeatingType

class RepeatingTypeAdapter: BaseAdapter() {

    private val repeatingTypes = RepeatingType.values()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_spinner_item, parent, false)
        val text1 = _convertView.findViewById<TextView>(android.R.id.text1)
        text1.text = parent.resources.getString(repeatingTypes[position].toStringRes())
        return _convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val _convertView: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val text1 = _convertView.findViewById<TextView>(android.R.id.text1)
        text1.text = parent.resources.getString(repeatingTypes[position].toStringRes())
        return _convertView
    }

    override fun getItem(position: Int): Any =
        repeatingTypes[position]

    override fun getItemId(position: Int): Long =
        repeatingTypes[position].ordinal.toLong()

    override fun getCount(): Int =
        repeatingTypes.size
}