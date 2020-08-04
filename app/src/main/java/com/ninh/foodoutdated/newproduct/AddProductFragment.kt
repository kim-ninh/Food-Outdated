package com.ninh.foodoutdated.newproduct

import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ninh.foodoutdated.AlarmUtils
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.editproduct.EditProductFragment

class AddProductFragment : EditProductFragment() {

    override fun inflateToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.add_product)
    }

    override fun loadProductFromDB() {
        productViewModel.loadDefaultProduct()
    }

    override val actionToDatePickerFragmentFunc =
        (AddProductFragmentDirections)::actionAddProductFragmentToDatePickerFragment

    override val actionToNumberPickerFragmentFunc =
        (AddProductFragmentDirections)::actionAddProductFragmentToNumberPickerFragment

    override val actionToReminderPickerFragmentFunc =
        (AddProductFragmentDirections)::actionAddProductFragmentToReminderPickerFragment

    override val actionToProductThumbActionFragmentFunc =
        (AddProductFragmentDirections)::actionAddProductFragmentToProductThumbActionFragment

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
                            val remindInfo = productViewModel.productAndRemindInfo.value!!.remindInfo
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

    override fun onDestroyImp() = Unit

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