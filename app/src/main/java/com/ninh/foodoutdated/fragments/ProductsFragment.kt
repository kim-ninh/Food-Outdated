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
import com.ninh.foodoutdated.ProductAdapter
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.viewmodels.ProductViewModel

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private lateinit var productRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productAdapter = ProductAdapter()

        productRecyclerView = view.findViewById(R.id.products_recycler_view)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productRecyclerView.adapter = productAdapter

        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ProductViewModel::class.java)
            .allProducts.observe(
                viewLifecycleOwner
            ) { products ->
                productAdapter.updateList(products)
            }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProductsFragment()
    }
}