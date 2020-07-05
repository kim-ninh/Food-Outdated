package com.ninh.foodoutdated

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Product(
    val name: String = "",
    val expiry: Date = Date(),      //date format: yyyy-mm-dd
    val thumbnail: String = ""
) : Parcelable {

    var id: Long = 0

    private val remainDayBeginWarn = 15

    // 30 day - 15 day - 0day
    // new - nearly expiry - expired
    val state: EXPIRY_STATE
        get() {
            var state = EXPIRY_STATE.NEW
            val now = Calendar.getInstance()
            val elapsedDate = DateUtils.substract(expiry, now.time)
            if (0 < elapsedDate && elapsedDate <= remainDayBeginWarn) {
                state = EXPIRY_STATE.NEARLY_EXPIRY
            } else if (elapsedDate <= 0) {
                state = EXPIRY_STATE.EXPIRED
            }
            return state
        }

    enum class EXPIRY_STATE {
        EXPIRED, NEARLY_EXPIRY, NEW
    }

    init {
        id = ++lastID
    }

    // Parcelable implement
    protected constructor(`in`: Parcel)
            : this(
        name = `in`.readString()!!,
        expiry = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(`in`.readString()!!)!!,
        thumbnail = `in`.readString()!!
    ) {
        lastID--
        id = `in`.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(thumbnail)
        dest.writeString(SimpleDateFormat(Utils.Companion.DATE_PATTERN_VN).format(expiry))
    }

    companion object {
        private var lastID: Long = 99
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(`in`: Parcel): Product? {
                return Product(`in`)
            }

            override fun newArray(size: Int): Array<Product?> {
                return arrayOfNulls(size)
            }
        }
    }
}