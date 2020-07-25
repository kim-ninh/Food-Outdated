package com.ninh.foodoutdated.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninh.foodoutdated.data.dao.ProductDao
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import java.util.concurrent.ExecutorService

class ProductRepo(
    private val executor: ExecutorService,
    private val productDao: ProductDao
) {

    private val _newProductIdObservable = MutableLiveData<Long>()
    private val _totalRowDeletedObserver = MutableLiveData<Int>()
    private val _newProductIdAndRemindInfoCode = MutableLiveData<Pair<Long, Int>>()

    val totalRowDeletedObserver: LiveData<Int>
        get() = _totalRowDeletedObserver
    val newProductIdObservable: LiveData<Long>
        get() = _newProductIdObservable
    val newProductIdAndRemindInfoCode: LiveData<Pair<Long, Int>>
        get() = _newProductIdAndRemindInfoCode
    val allProducts: LiveData<List<Product>> = productDao.loadAllProducts()

    fun insert(productAndRemindInfo: ProductAndRemindInfo): LiveData<Pair<Long, Int>>{
        val pairObservable = MutableLiveData<Pair<Long, Int>>()
        executor.submit {
            val pair = productDao.insertProductWithRemindInfo(productAndRemindInfo)
            _newProductIdAndRemindInfoCode.postValue(pair)
            pairObservable.postValue(pair)
        }
        return pairObservable
    }

    fun updateProduct(productAndRemindInfo: ProductAndRemindInfo){
        executor.submit {
            productDao.updateProductWithRemindInfo(productAndRemindInfo)
        }
    }

    fun deleteProductById(productId: Long) {
        executor.submit {
            productDao.deleteProductById(productId)
        }
    }

    fun deleteProductsByIds(productIds: LongArray) {
        executor.submit {
            val numOfRowAffected = productDao.deleteProductsById(productIds)
            _totalRowDeletedObserver.postValue(numOfRowAffected)
        }
    }

    fun loadProductById(id: Long) =
        productDao.loadProductById(id)


    fun loadProductAndRemindInfo(id: Long) =
        productDao.loadProductAndRemindInfo(id)

}