package com.ninh.foodoutdated.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.FoodOutdatedApplication
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.repo.ProductRepo

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepo: ProductRepo

    val allProducts: LiveData<List<Product>>
    val totalRowDeleted: LiveData<Int>

    init {
        val executor = (application as FoodOutdatedApplication).workerExecutor
        val productDao = ProductDatabase.getDatabase(application.applicationContext)
            .productDao()

        productRepo = ProductRepo(executor, productDao)
        allProducts = productRepo.allProducts
        totalRowDeleted = productRepo.totalRowDeletedObserver
    }

    fun load(id: Int) =
        productRepo.loadAsync(id)

    fun insert(productAndRemindInfo: ProductAndRemindInfo) =
        productRepo.insert(productAndRemindInfo)

    fun update(productAndRemindInfo: ProductAndRemindInfo){
        productRepo.update(productAndRemindInfo)
    }

    fun delete(productIds: IntArray) {
        productRepo.delete(productIds)
    }
}