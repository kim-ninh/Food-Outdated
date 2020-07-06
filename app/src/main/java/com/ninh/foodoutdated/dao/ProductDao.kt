package com.ninh.foodoutdated.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ninh.foodoutdated.models.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY expiry DESC")
    fun loadAllProducts(): LiveData<List<Product>>

    @Insert
    fun insertProduct(product: Product)

    @Delete
    fun deleteProducts(products: List<Product>)
}