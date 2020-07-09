package com.ninh.foodoutdated.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.*
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var selectionTracker: SelectionTracker<Long>

    private var actionMode: ActionMode? = null

    private var actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.delete_context, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            val totalSelection = selectionTracker.selection.size()
            mode.title = "$totalSelection item selected"
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            if (selectionTracker.selection.size() != 0){
                selectionTracker.clearSelection()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productAdapter = ProductAdapter()
        val itemSpacingInPixel = resources.getDimensionPixelSize(R.dimen.item_spacing)

        productRecyclerView = view.findViewById(R.id.products_recycler_view)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productRecyclerView.adapter = productAdapter
        productRecyclerView.addItemDecoration(SpacingItemDecoration(itemSpacingInPixel))
        selectionTracker = SelectionTracker.Builder(
            "product-selection-id",
            productRecyclerView,
            ProductItemKeyProvider(productAdapter),
            ProductItemDetailsLookup(productRecyclerView),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()

                val totalSelection = selectionTracker.selection.size()
                if (actionMode == null && totalSelection == 1){
                    actionMode = (requireActivity() as AppCompatActivity).startSupportActionMode(actionModeCallback)
                }
                actionMode?.invalidate()
                if (totalSelection == 0){
                    actionMode?.finish()
                }
            }
        })

        productAdapter.tracker = selectionTracker

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