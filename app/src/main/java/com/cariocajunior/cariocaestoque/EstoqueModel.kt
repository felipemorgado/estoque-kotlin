package com.cariocajunior.cariocaestoque

import android.os.Parcel
import android.os.Parcelable

data class EstoqueModel(
    var id: Int = -1,
    var nome: String = "",
    var modelo: String = "",
    var qtd: Int = 0,
    var peso: Int = 0,
    var description: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nome)
        parcel.writeString(modelo)
        parcel.writeInt(qtd)
        parcel.writeInt(peso)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EstoqueModel> {
        override fun createFromParcel(parcel: Parcel): EstoqueModel {
            return EstoqueModel(parcel)
        }

        override fun newArray(size: Int): Array<EstoqueModel?> {
            return arrayOfNulls(size)
        }
    }
}



