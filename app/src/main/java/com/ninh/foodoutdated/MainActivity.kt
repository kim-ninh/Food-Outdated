package com.ninh.foodoutdated

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ninh.foodoutdated.databinding.ActivityMainBinding
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
            val dest: String = try {
                resources.getResourceName(destination.id)
            }catch (e: Resources.NotFoundException){
                Integer.toString(destination.id)
            }

            Logger.d("Navigated to $dest")

            if (destination.id == R.id.productsFragment){
                floatingActionButton.visibility = View.VISIBLE
            }else{
                floatingActionButton.visibility = View.GONE
            }
        }

        floatingActionButton.setOnClickListener {
            val action = ProductsFragmentDirections
                .actionProductsFragmentToAddProductFragment()
            navController.navigate(action)
        }
    }
}