package com.ninh.foodoutdated.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.ProductAdapter
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.SpacingItemDecoration
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private lateinit var productRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productAdapter = ProductAdapter()
        val itemSpacingInPixel = resources.getDimensionPixelSize(R.dimen.item_spacing)

        productRecyclerView = view.findViewById(R.id.products_recycler_view)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productRecyclerView.adapter = productAdapter
        productRecyclerView.addItemDecoration(SpacingItemDecoration(itemSpacingInPixel))

        val productViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)

        productViewModel.run {
            allProducts.observe(
                viewLifecycleOwner
            ) { products ->
                Logger.i("Product changed!: ${products.size}")
                productAdapter.updateList(products)
            }

            newProductId.observe(
                viewLifecycleOwner
            ) { newId ->
                Snackbar.make(
                    productRecyclerView, "Add product successful",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Action", null).show()
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProductsFragment()
    }
}