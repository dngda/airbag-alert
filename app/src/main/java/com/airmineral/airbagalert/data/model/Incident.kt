package com.airmineral.airbagalert.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Incident(
    @DocumentId
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val time: String,
    val car_id: String,
    val car_model: String,
    val damaged: String,
    val handled_by: String,
) : Parcelable {

    constructor() : this("", 0.0, 0.0, "", "", "", "", "", "")

    companion object {
        const val COLLECTION_NAME = "incidents"
        val DUMMY = Incident(
            "123",
            -7.771915,
            110.387036,
            "9-11-2022",
            "12:00",
            "AB 1231 CD",
            "Toyota Avanza",
            "Ajur remuk ngasi koyo roti bakar ngarepan FT",
            ""
        )


    }

    fun convertDate(input: String): String {
        val inpSp = input.split("-")
        val date = Calendar.getInstance()
        date.set(Calendar.DAY_OF_MONTH, inpSp[0].toInt())
        date.set(Calendar.MONTH, inpSp[1].toInt() - 1)
        date.set(Calendar.YEAR, inpSp[2].toInt())
        return "${
            date.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault()
            )
        }, ${date.get(Calendar.DAY_OF_MONTH)} ${
            date.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault()
            )
        } ${date.get(Calendar.YEAR)}"
    }
}