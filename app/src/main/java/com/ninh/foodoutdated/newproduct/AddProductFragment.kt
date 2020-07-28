package com.ninh.foodoutdated.newproduct

import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ninh.foodoutdated.AlarmUtils
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.RepeatingType
import com.ninh.foodoutdated.editproduct.EditProductFragment
import java.util.*

class AddProductFragment : EditProductFragment() {

    override fun inflateToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.add_product)
    }

    override fun loadProductFromDB() {
        productViewModel.loadDefaultProduct()
    }

    override fun getActionToDatePickerFragment(pickingDate: Calendar) =
        AddProductFragmentDirections.actionAddProductFragmentToDatePickerFragment(pickingDate)

    override fun getActionToNumberPickerFragment(
        startValue: Int,
        endValue: Int,
        selectedValue: Int
    ) =
        AddProductFragmentDirections.actionAddProductFragmentToNumberPickerFragment(
            startValue,
            endValue,
            selectedValue
        )

    override fun getActionToReminderPickerFragment(
        expiry: Calendar,
        triggerDate: Calendar?,
        triggerTime: Calendar?,
        repeatingType: RepeatingType
    ) =
        AddProductFragmentDirections.actionAddProductFragmentToReminderPickerFragment(
            expiry,
            triggerDate,
            triggerTime,
            repeatingType
        )

    override fun getActionToProductThumbActionFragment(photoFilePath: String?) =
        AddProductFragmentDirections.actionAddProductFragmentToProductThumbActionFragment(
            photoFilePath
        )

    override fun getThisBackStackEntry(navController: NavController) =
        navController.getBackStackEntry(R.id.addProductFragment)

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                if (validate()) {
                    productsViewModel
                        .insert(productViewModel.productAndRemindInfo.value!!)
                        .observe(this) { newId ->
                            Log.i(
                                TAG,
                                "product inserted, id: $newId"
                            )
                            val remindInfo =
                                productViewModel.productAndRemindInfo.value!!.remindInfo
                            remindInfo.productId = newId
                            AlarmUtils.add(requireContext(), remindInfo)
                            findNavController()
                                .previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(KEY_IS_ADD_SUCCESSFUL, true)
                            findNavController().navigateUp()
                        }
                } else {
                    findNavController().navigateUp()
                }
                true
            }
            else -> false
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        val productName = binding.name.text
        if (productName!!.isEmpty() || productName.isBlank()) {
            isValid = false
        }

        return isValid
    }

    companion object {
        val TAG = AddProductFragment::class.java.simpleName

        const val KEY_IS_ADD_SUCCESSFUL = "IS_ADD_SUCCESSFUL"
    }
}