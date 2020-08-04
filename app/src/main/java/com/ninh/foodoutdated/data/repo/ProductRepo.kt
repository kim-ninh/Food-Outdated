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
    private val _totalRowDeletedObserver = MutableLiveData<Int>()

    val totalRowDeletedObserver: LiveData<Int>
        get() = _totalRowDeletedObserver


    val allProducts: LiveData<List<Product>> = productDao.loadAllAsync()

    fun loadAsync(id: Int) =
        productDao.loadAsync(id)

    fun load(id: Int) =
        productDao.load(id)

    fun insert(productAndRemindInfo: ProductAndRemindInfo): LiveData<Int>{
        val idObservable = MutableLiveData<Int>()
        executor.submit {
            val id = productDao.insert(productAndRemindInfo)
            idObservable.postValue(id)
        }
        return idObservable
    }

    fun update(productAndRemindInfo: ProductAndRemindInfo){
        executor.submit {
            productDao.update(productAndRemindInfo)
        }
    }

    fun delete(productId: Int) {
        executor.submit {
            productDao.delete(productId)
        }
    }

    fun delete(productIds: IntArray) {
        executor.submit {
            val numOfRowAffected = productDao.delete(productIds)
            _totalRowDeletedObserver.postValue(numOfRowAffected)
        }
    }
}