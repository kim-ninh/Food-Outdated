package com.ninh.foodoutdated.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninh.foodoutdated.dao.ProductDao
import com.ninh.foodoutdated.models.Product
import java.util.concurrent.ExecutorService

class ProductRepo(
    private val executor: ExecutorService,
    private val productDao: ProductDao
) {

    private val _newProductIdObservable = MutableLiveData<Long>()

    val newProductIdObservable: LiveData<Long>
        get() = _newProductIdObservable
    val allProducts: LiveData<List<Product>> = productDao.loadAllProducts()

    fun insertProduct(product: Product) {
        executor.submit {
            val id = productDao.insertProduct(product)
            _newProductIdObservable.postValue(id)
        }
    }

    fun deleteProducts(products: List<Product>){
        executor.submit{
            productDao.deleteProducts(products)
        }
    }
}