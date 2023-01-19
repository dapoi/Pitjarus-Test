package com.dapoi.pitjarustest.data.source.remote.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class LoginResponse(

    @Json(name = "stores")
    val stores: List<StoresItem>,

    @Json(name = "message")
    val message: String,

    @Json(name = "status")
    val status: String
)

@Entity(tableName = "stores")
@Parcelize
data class StoresItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    var has_visit: Boolean = false,

    @Json(name = "store_id")
    val store_id: String,

    @Json(name = "store_code")
    val store_code: String,

    @Json(name = "channel_name")
    val channel_name: String,

    @Json(name = "area_name")
    val area_name: String,

    @Json(name = "address")
    val address: String,

    @Json(name = "dc_name")
    val dc_name: String,

    @Json(name = "latitude")
    val latitude: String,

    @Json(name = "region_id")
    val region_id: String,

    @Json(name = "area_id")
    val area_id: String,

    @Json(name = "account_id")
    val account_id: String,

    @Json(name = "dc_id")
    val dc_id: String,

    @Json(name = "subchannel_id")
    val subchannel_id: String,

    @Json(name = "account_name")
    val account_name: String,

    @Json(name = "store_name")
    val store_name: String,

    @Json(name = "subchannel_name")
    val subchannel_name: String,

    @Json(name = "region_name")
    val region_name: String,

    @Json(name = "channel_id")
    val channel_id: String,

    @Json(name = "longitude")
    val longitude: String
) : Parcelable
