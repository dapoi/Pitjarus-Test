package com.dapoi.pitjarustest.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem

@Database(entities = [StoresItem::class], version = 1, exportSchema = false)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}