package com.dapoi.pitjarustest.data.source

import com.dapoi.pitjarustest.data.source.remote.model.LoginResponse
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ShopRepository {

    fun login(username: String, password: String): Flow<Resource<LoginResponse>>

    suspend fun insertShop(shop: List<StoresItem>)

    fun getShop(): Flow<Resource<List<StoresItem>>>
}