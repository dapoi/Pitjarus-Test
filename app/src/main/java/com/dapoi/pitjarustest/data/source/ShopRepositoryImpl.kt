package com.dapoi.pitjarustest.data.source

import com.dapoi.pitjarustest.data.source.local.ShopDao
import com.dapoi.pitjarustest.data.source.remote.model.LoginRequest
import com.dapoi.pitjarustest.data.source.remote.model.LoginResponse
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.data.source.remote.network.ApiService
import com.dapoi.pitjarustest.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val shopDao: ShopDao
) : ShopRepository {

    override fun login(username: String, password: String): Flow<Resource<LoginResponse>> {
        return flow {
            emit(Resource.Loading())
            try {
//                val loginRequest = LoginRequest(username, password)
                val response = apiService.login(username, password)
                if (response.status == "success") {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Error(response.message))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun insertShop(shop: List<StoresItem>) {
        shopDao.insertShop(shop)
    }

    override fun getShop(): Flow<Resource<List<StoresItem>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = shopDao.getShop()
                if (response.isNotEmpty()) {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Error("Data not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}