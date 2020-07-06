package com.ninh.foodoutdated.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.dao.ProductDao
import com.ninh.foodoutdated.models.Product
import java.util.concurrent.ExecutorService

class ProductRepo(
    private val executor: ExecutorService,
    private val productDao: ProductDao
) {

    val allProducts: LiveData<List<Product>> = productDao.loadAllProducts()

    fun insertProduct(product: Product) {
        executor.submit {
            productDao.insertProduct(product)
        }
    }

    fun deleteProducts(products: List<Product>){
        executor.submit{
            productDao.deleteProducts(products)
        }
    }
}