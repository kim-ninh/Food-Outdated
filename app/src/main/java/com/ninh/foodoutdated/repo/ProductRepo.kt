package com.ninh.foodoutdated.repo

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
    private val _totalRowDeletedObserver = MutableLiveData<Int>()

    val totalRowDeletedObserver: LiveData<Int>
        get() = _totalRowDeletedObserver
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

    fun deleteProductsByIds(productIds: LongArray){
        executor.submit {
            val numOfRowAffected = productDao.deleteProductsById(productIds)
            _totalRowDeletedObserver.postValue(numOfRowAffected)
        }
    }
}