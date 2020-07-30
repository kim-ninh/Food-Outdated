package com.ninh.foodoutdated.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.dialogfragments.TriggerDate
import java.io.File
import java.util.*

class ProductViewModel: ViewModel() {

    private val _thumb = MutableLiveData<File?>()
    val thumb: LiveData<File?>
        get() = _thumb

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int>
        get() = _quantity

    private val _expiry = MutableLiveData<Calendar>()
    val expiry: LiveData<Calendar>
        get() = _expiry

    private val _reminder = MutableLiveData<Calendar>()
    val reminder: LiveData<Calendar>
        get() = _reminder

    private val _productAndRemindInfo = MutableLiveData<ProductAndRemindInfo>()
    val productAndRemindInfo: LiveData<ProductAndRemindInfo>
        get() = _productAndRemindInfo

    fun setThumb(thumb: File?) {
        _thumb.value = thumb

        productAndRemindInfo.value?.apply {
            product.thumb = thumb
        }
    }

    fun setName(name: String) {
        _name.value = name

        productAndRemindInfo.value?.apply {
            product.name = name
        }
    }

    fun setQuantity(quantity: Int) {
        _quantity.value = quantity

        productAndRemindInfo.value?.apply {
            product.quantity = quantity
        }
    }

    fun setExpiry(expiry: Calendar) {
        _expiry.value = expiry

        productAndRemindInfo.value?.apply {
            product.expiry = expiry

            val defaultTriggerDateValue = TriggerDate.A_WEEK_BEFORE.getValueFromExpiry(expiry)
            remindInfo.triggerDate.timeInMillis = defaultTriggerDateValue.timeInMillis
            _reminder.value = remindInfo.triggerDate
        }
    }

    fun setReminder(reminder: RemindInfo) {
        _reminder.value = reminder.triggerDate

        productAndRemindInfo.value?.apply {
            remindInfo.triggerDate.timeInMillis = reminder.triggerDate.timeInMillis
            remindInfo.repeating = reminder.repeating
        }
    }

    fun setProduct(productAndRemindInfo: ProductAndRemindInfo) = with(productAndRemindInfo) {
        _productAndRemindInfo.value = this
        setThumb(product.thumb)
        setName(product.name)
        setQuantity(product.quantity)
        setExpiry(product.expiry)
        setReminder(remindInfo)
    }

    fun loadDefaultProduct(){
        val defaultProductAndRemindInfo = ProductAndRemindInfo(Product(), RemindInfo())
        setProduct(defaultProductAndRemindInfo)
    }
}