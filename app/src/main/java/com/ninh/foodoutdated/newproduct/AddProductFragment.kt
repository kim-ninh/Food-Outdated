package com.ninh.foodoutdated.newproduct

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.editproduct.EditProductFragment

class AddProductFragment : EditProductFragment() {

    override fun inflateToolbarMenu() {
        toolBar.inflateMenu(R.menu.add_product)
    }

    override fun loadProductFromDB() = Unit

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_add ->{
                if (validate()){
                    product?.let {
                        productViewModel.insert(it)
                    }
                }

                val action =
                    AddProductFragmentDirections.actionAddProductFragmentToProductsFragment()
                findNavController().navigate(action)
                true
            }
            else -> false
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        val productName = productEditText.text
        if (productName.isEmpty() || productName.isBlank()) {
            isValid = false
        }

        return isValid
    }
}