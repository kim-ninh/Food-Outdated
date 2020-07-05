package com.ninh.foodoutdated

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Product(
    val id: Long = ++lastID,
    val name: String = "",
    val expiry: Date = Date(),      //date format: yyyy-mm-dd
    val thumbnail: String = ""
) : Parcelable {

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

    // Parcelable implement
    protected constructor(`in`: Parcel)
            : this(
        id = `in`.readLong(),
        name = `in`.readString()!!,
        expiry = SimpleDateFormat(Utils.DATE_PATTERN_VN).parse(`in`.readString()!!)!!,
        thumbnail = `in`.readString()!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(SimpleDateFormat(Utils.Companion.DATE_PATTERN_VN).format(expiry))
        dest.writeString(thumbnail)
    }

    companion object CREATOR: Parcelable.Creator<Product>{
        private var lastID: Long = 99

        override fun createFromParcel(source: Parcel): Product {
            return Product(source)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }

    }
}