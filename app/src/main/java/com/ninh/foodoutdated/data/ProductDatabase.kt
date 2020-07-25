package com.ninh.foodoutdated.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ninh.foodoutdated.data.dao.ProductDao
import com.ninh.foodoutdated.data.dao.RemindInfoDao
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.RemindInfo

@Database(entities = [Product::class, RemindInfo::class], version = 1)
@TypeConverters(Converters::class)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun remindInfoDao(): RemindInfoDao

    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            return INSTANCE
                ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}