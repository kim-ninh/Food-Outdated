package com.ninh.foodoutdated.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.ProductDatabase
import com.ninh.foodoutdated.models.Product
import com.ninh.foodoutdated.repo.ProductRepo

class ProductViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ProductRepo

    val allProducts: LiveData<List<Product>>
    val newProductId: LiveData<Long>
    val totalRowDeleted: LiveData<Int>

    init {
        val executor = (application as MyApplication).workerExecutor
        val productDao = ProductDatabase.getDatabase(application.applicationContext)
            .productDao()

        repository = ProductRepo(executor, productDao)
        allProducts = repository.allProducts
        newProductId = repository.newProductIdObservable
        totalRowDeleted = repository.totalRowDeletedObserver
    }

    fun insert(product: Product){
        repository.insertProduct(product)
    }

    fun delete(products: List<Product>){
        repository.deleteProducts(products)
    }

    fun deleteByIds(productIds: LongArray){
        repository.deleteProductsByIds(productIds)
    }
}