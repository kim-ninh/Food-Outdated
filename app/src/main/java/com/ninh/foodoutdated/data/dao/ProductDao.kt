package com.ninh.foodoutdated.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.models.RemindInfo

@Dao
abstract class ProductDao {

    @Query("SELECT * FROM products WHERE isValid = 1 ORDER BY expiry ASC")
    abstract fun loadAll(): LiveData<List<Product>>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId AND isValid = 1 LIMIT 1")
    abstract fun load(productId: Int): LiveData<ProductAndRemindInfo>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun _insertProduct(product: Product): Long

    @Insert
    abstract fun _insertRemindInfo(remindInfo: RemindInfo)

    fun insert(productAndRemindInfo: ProductAndRemindInfo): Int {
        val productId = _insertProduct(productAndRemindInfo.product).toInt()
        productAndRemindInfo.remindInfo.productId = productId
        _insertRemindInfo(productAndRemindInfo.remindInfo)
        return productId
    }


    @Update
    abstract fun _updateProduct(product: Product)

    @Update
    abstract fun _updateRemindInfo(remindInfo: RemindInfo)

    fun update(productAndRemindInfo: ProductAndRemindInfo){
        _updateProduct(productAndRemindInfo.product)
        _updateRemindInfo(productAndRemindInfo.remindInfo)
    }


    @Query("UPDATE products set isValid = 0 WHERE id = :productId ")
    abstract fun _deleteProduct(productId: Int): Int

    @Query("UPDATE remind_info SET isValid = 0 WHERE productId = :id")
    abstract fun _deleteRemindInfo(id: Int)

    fun delete(id: Int){
        _deleteProduct(id)
        _deleteRemindInfo(id)
    }

    @Query("UPDATE products set isValid = 0 WHERE id IN (:productIds)")
    abstract fun _deleteProducts(productIds: IntArray): Int

    @Query("UPDATE remind_info SET isValid = 0 WHERE productId IN (:ids)")
    abstract fun _deleteRemindInfo(ids: IntArray)


    fun delete(ids: IntArray): Int{
        val affectedRows = _deleteProducts(ids)
        _deleteRemindInfo(ids)
        return affectedRows
    }
}