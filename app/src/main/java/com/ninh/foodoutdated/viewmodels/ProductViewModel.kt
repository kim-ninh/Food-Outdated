package com.ninh.foodoutdated.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.repo.ProductRepo
import com.ninh.foodoutdated.data.repo.RemindInfoRepo

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepo: ProductRepo

    val allProducts: LiveData<List<Product>>
    val newProductId: LiveData<Long>
    val totalRowDeleted: LiveData<Int>
    val productIdAndRemindInfoCode: LiveData<Pair<Long, Int>>

    init {
        val executor = (application as MyApplication).workerExecutor
        val productDao = ProductDatabase.getDatabase(application.applicationContext)
            .productDao()

        productRepo = ProductRepo(executor, productDao)
        allProducts = productRepo.allProducts
        newProductId = productRepo.newProductIdObservable
        totalRowDeleted = productRepo.totalRowDeletedObserver
        productIdAndRemindInfoCode = productRepo.newProductIdAndRemindInfoCode
    }

    fun insert(product: Product) {
        productRepo.insertProduct(product)
    }

    fun insert(productAndRemindInfo: ProductAndRemindInfo) =
        productRepo.insert(productAndRemindInfo)


    fun deleteByIds(productIds: LongArray) {
        productRepo.deleteProductsByIds(productIds)
    }

    fun loadById(id: Long) =
        productRepo.loadProductById(id)

    fun loadProductAndRemindInfo(id: Long) =
        productRepo.loadProductAndRemindInfo(id)
}