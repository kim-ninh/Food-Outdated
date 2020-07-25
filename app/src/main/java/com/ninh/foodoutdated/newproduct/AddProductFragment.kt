package com.ninh.foodoutdated.newproduct

import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.editproduct.EditProductFragment

class AddProductFragment : EditProductFragment() {

    override fun inflateToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.add_product)
    }

    override fun loadProductFromDB() = Unit

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                if (validate()) {
                    productViewModel.insert(
                        ProductAndRemindInfo(
                            product,
                            binding.content.reminder.remindInfo
                        )
                    ).observe(this) {
                        Log.i(
                            TAG,
                            "product inserted, id: ${it.first} remind info request code: ${it.second}"
                        )

                        val action =
                            AddProductFragmentDirections.actionAddProductFragmentToProductsFragment()
                        findNavController().navigate(action)
                    }
                } else {
                    val action =
                        AddProductFragmentDirections.actionAddProductFragmentToProductsFragment()
                    findNavController().navigate(action)
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
    }
}