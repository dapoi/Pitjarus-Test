package com.dapoi.pitjarustest.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem

@Dao
interface ShopDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShop(shop: List<StoresItem>)

    @Query("SELECT * FROM stores")
    suspend fun getShop(): List<StoresItem>
}