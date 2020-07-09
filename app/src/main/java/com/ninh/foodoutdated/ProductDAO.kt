package com.ninh.foodoutdated

import android.content.Context
import com.orhanobut.logger.Logger
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class ProductDAO internal constructor(private val context: Context) {
    private var products: MutableList<Product> = ArrayList()
    private val FILE_NAME = "data.json"
    private val isFileExists: Boolean
        private get() {
            val dataFile = File(context.filesDir, FILE_NAME)
            return dataFile.exists()
        }

    private fun loadFromFile(): MutableList<Product> {
        var productList: MutableList<Product> = ArrayList()
        var data = ""
        val inputStream: FileInputStream
        val reader: BufferedReader
        try {
            inputStream = context.openFileInput(FILE_NAME)
            reader = BufferedReader(FileReader(inputStream.fd))
            data = reader.readLine()
//            val productType = object : TypeToken<List<Product?>?>() {}.type
//            productList = gson.fromJson<MutableList<Product>>(data, productType)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return productList
    }

    private fun saveToFile() {

    }

    fun loadAll(): List<Product> {
        return products
    }

    fun loadById(id: Long): Product? {
        var product: Product? = null
        for (p in products!!) {
            if (p.id == id) {
                product = p
            }
        }
        return product
    }

    fun findIndex(id: Long): Int {
        var index = -1
        for (i in products!!.indices) {
            if (products!![i].id == id) {
                index = i
            }
        }
        return index
    }

    fun deleteById(id: Long): Int {
        val index = findIndex(id)
        if (index != -1) {
            products!!.removeAt(index)
        }
        return index
    }

    fun add(product: Product) {
        products!!.add(product)
        saveToFile()
    }

    fun size(): Int {
        return products!!.size
    }

    init {
        if (isFileExists) {
            products = loadFromFile()
        }
    }
}