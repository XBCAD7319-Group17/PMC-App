package com.pmcmaApp.pmcma

import android.os.Parcel
import android.os.Parcelable

data class NGO(
    val name: String,
    val logo: Int,
    val description: String,
    val images: List<Int>,
    val email: String,
    val phone: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createIntArray()?.toList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(logo)
        parcel.writeString(description)
        parcel.writeIntArray(images.toIntArray())
        parcel.writeString(email)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NGO> {
        override fun createFromParcel(parcel: Parcel): NGO {
            return NGO(parcel)
        }

        override fun newArray(size: Int): Array<NGO?> {
            return arrayOfNulls(size)
        }
    }
}
