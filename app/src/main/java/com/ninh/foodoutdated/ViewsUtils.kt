package com.ninh.foodoutdated

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout

fun EditText.hideSoftKeyboard() {
    val imm: InputMethodManager? = this.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    if (imm != null && imm.isActive(this)){
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

val View.resourceEntryName: String
    get() = context.resources.getResourceEntryName(id)

fun CollapsingToolbarLayout.hideTitle(){
    title = ""
}

fun <T : View> Fragment.findViewById(@IdRes id: Int): T{
    return view!!.findViewById(id)
}