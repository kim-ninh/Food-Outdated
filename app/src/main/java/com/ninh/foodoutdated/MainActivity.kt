package com.ninh.foodoutdated

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ninh.foodoutdated.databinding.ActivityMainBinding
import com.ninh.foodoutdated.editproduct.EditProductFragmentDirections
import com.ninh.foodoutdated.extensions.getResourceNameOrNull
import com.ninh.foodoutdated.mainlist.ProductsFragmentDirections

import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val floatingActionButton = binding.fabAdd
        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val dest = resources.getResourceNameOrNull(destination.id)
                ?: destination.id

            Logger.d("Navigated to $dest")

            floatingActionButton.visibility = when(destination.id){
                R.id.productsFragment -> View.VISIBLE
                else -> View.GONE
            }
        }

        floatingActionButton.setOnClickListener {
            val action = ProductsFragmentDirections
                .actionProductsFragmentToAddProductFragment()
            navController.navigate(action)
        }

        val extras = intent.extras
        if (extras != null){
            val productId = extras.getInt(KEY_PRODUCT_ID)
            extras.remove(KEY_PRODUCT_ID)
            if (productId != 0){
                val action = ProductsFragmentDirections
                    .actionProductsFragmentToEditProductFragment(productId)
                navController.navigate(action)
            }
        }
    }

    companion object {
        private const val KEY_PRODUCT_ID = "PRODUCT_ID"

        fun newIntent(context: Context, productId: Int) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_PRODUCT_ID, productId)
            }
    }
}