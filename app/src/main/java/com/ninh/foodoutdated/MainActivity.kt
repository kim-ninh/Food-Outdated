package com.ninh.foodoutdated

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ninh.foodoutdated.fragments.ProductsFragmentDirections
import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_action_button)
        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            }catch (e: Resources.NotFoundException){
                Integer.toString(destination.id)
            }

            Logger.d("Navigated to $dest")
        }

        floatingActionButton.setOnClickListener {
            it.visibility = View.GONE
            val action = ProductsFragmentDirections
                .actionProductsFragmentToAddProductFragment()
            navController.navigate(action)
        }
    }
}