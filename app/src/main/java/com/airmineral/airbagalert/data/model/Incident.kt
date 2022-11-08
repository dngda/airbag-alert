package com.airmineral.airbagalert.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Incident(
    @DocumentId
    val id: String,
    val latitude: String,
    val longitude: String,
    val date: String,
    val time: String,
    val car_id: String,
    val car_model: String,
    val damaged: String,
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "")

    companion object {
        const val COLLECTION_NAME = "incidents"
        val EMPTY = Incident("", "", "", "", "", "", "", "")
        val DUMMY = Incident(
            "123",
            "-7.771915",
            "110.387036",
            "Senin, 18 Oktober 2022",
            "21:00",
            "AB 1231 CD",
            "Toyota Avanza",
            "Ajur remuk ngasi koyo roti bakar ngarepan FT"
        )

    }
}