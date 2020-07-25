package com.ninh.foodoutdated.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.models.RemindInfo

@Dao
abstract class ProductDao {

    @Query("SELECT * FROM products WHERE isValid = 1 ORDER BY expiryDate ASC")
    abstract fun loadAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId AND isValid = 1 LIMIT 1")
    abstract fun loadProductById(productId: Long): LiveData<Product>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId AND isValid = 1 LIMIT 1")
    abstract fun loadProductAndRemindInfo(productId: Long): LiveData<ProductAndRemindInfo>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertProduct(product: Product): Long

    @Update
    abstract fun updateProduct(product: Product)

    @Query("UPDATE products set isValid = 0 WHERE id = :productId ")
    abstract fun deleteProductById(productId: Long): Int

    @Query("UPDATE products set isValid = 0 WHERE id IN (:productIds)")
    abstract fun deleteProductsById(productIds: LongArray): Int

    fun insertProductWithRemindInfo(productAndRemindInfo: ProductAndRemindInfo): Pair<Long, Int>{
        val productId = insertProduct(productAndRemindInfo.product)
        productAndRemindInfo.remindInfo.productOwnerId = productId
        val requestCode = insertRemindInfo(productAndRemindInfo.remindInfo).toInt()

        return Pair(productId, requestCode)
    }

    fun updateProductWithRemindInfo(productAndRemindInfo: ProductAndRemindInfo){
        updateProduct(productAndRemindInfo.product)
        updateRemindInfo(productAndRemindInfo.remindInfo)
    }

    fun deleteProductWithRemindInfo(productAndRemindInfo: ProductAndRemindInfo){
        deleteProductById(productAndRemindInfo.product.id!!)
        deleteRemindInfoByRequestCode(productAndRemindInfo.remindInfo.requestCode)
    }

    fun deleteProductWithRemindInfoByIds(ids: LongArray){
        deleteProductsById(ids)
        deleteRemindInfoByProductOwnerIds(ids)
    }

    @Insert
    abstract fun insertRemindInfo(remindInfo: RemindInfo): Long

    @Update
    abstract fun updateRemindInfo(remindInfo: RemindInfo)

    @Query("UPDATE remind_info SET isValid = 0 WHERE requestCode = :code")
    abstract fun deleteRemindInfoByRequestCode(code: Int)

    @Query("UPDATE remind_info SET isValid = 0 WHERE productOwnerId IN (:ids)")
    abstract fun deleteRemindInfoByProductOwnerIds(ids: LongArray)
}