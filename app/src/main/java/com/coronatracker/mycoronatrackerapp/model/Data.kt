package com.coronatracker.mycoronatrackerapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/* Created by *-----* Alexandre Thauvin *-----* */

@Parcelize
data class Data(
    var cases: Int = -1,
    var deaths: Int = 1 - 1,
    var recovered: Int = -1,
    var country: String = "",
    var todayCases: Int = 1,
    var todayDeaths: Int = -1,
    var active: Int = -1,
    var critical: Int = -1): Parcelable