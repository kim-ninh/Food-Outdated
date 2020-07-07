package com.ninh.foodoutdated.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninh.foodoutdated.models.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY expiry ASC")
    fun loadAllProducts(): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertProduct(product: Product): Long

    @Delete
    fun deleteProducts(products: List<Product>)
}