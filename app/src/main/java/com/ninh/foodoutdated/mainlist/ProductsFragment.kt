package com.ninh.foodoutdated.mainlist

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.*
import com.ninh.foodoutdated.databinding.FragmentProductsBinding
import com.ninh.foodoutdated.editproduct.EditProductFragmentDirections
import com.ninh.foodoutdated.viewmodels.ProductViewModel
import com.orhanobut.logger.Logger

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val productViewModel: ProductViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }
    private lateinit var selectionTracker: SelectionTracker<Long>

    private var _binding: FragmentProductsBinding? = null
    private val binding
        get() = _binding!!

    private var actionMode: ActionMode? = null

    private var actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.item_delete -> {
                    val selection = selectionTracker.selection
                    val selectedIds = LongArray(selection.size())
                    val it: Iterator<Long> = selection.iterator()
                    var i = 0
                    while (it.hasNext()) {
                        selectedIds[i++] = it.next()
                    }
                    productViewModel.deleteByIds(selectedIds)
                    true
                }
                else -> false
            }
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
            if (selectionTracker.selection.size() != 0) {
                selectionTracker.clearSelection()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductsBinding.bind(view)
        val productAdapter = ProductAdapter() { productId ->
            val action =
                ProductsFragmentDirections.actionProductsFragmentToEditProductFragment(productId)
            findNavController().navigate(action)
        }
        val itemSpacingInPixel = resources.getDimensionPixelSize(R.dimen.item_spacing)

        binding.productsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
            addItemDecoration(SpacingItemDecoration(itemSpacingInPixel))
        }

        selectionTracker = SelectionTracker.Builder(
            "product-selection-id",
            binding.productsRecyclerView,
            ProductItemKeyProvider(productAdapter),
            ProductItemDetailsLookup(
                binding.productsRecyclerView
            ),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()

                val totalSelection = selectionTracker.selection.size()
                if (actionMode == null && totalSelection == 1) {
                    actionMode = (requireActivity() as AppCompatActivity).startSupportActionMode(
                        actionModeCallback
                    )
                }
                actionMode?.invalidate()
                if (totalSelection == 0) {
                    actionMode?.finish()
                }
            }
        })

        productAdapter.tracker = selectionTracker

        productViewModel.run {
            allProducts.observe(
                viewLifecycleOwner
            ) { products ->
                Logger.i("Product changed!: ${products.size}")
                productAdapter.submitList(products)
            }

            totalRowDeleted.observe(
                viewLifecycleOwner
            ) { totalRow ->
                actionMode?.finish()
                Snackbar.make(
                    binding.root, "Deleted $totalRow item", Snackbar.LENGTH_LONG
                )
                    .setAction("Undo") {}.show()
            }
        }

//        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolBarFragProducts))
//        (requireActivity() as AppCompatActivity).supportActionBar!!.title = "Main title here"
//        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = ProductsFragment::class.java.simpleName
    }
}