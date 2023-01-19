package com.dapoi.pitjarustest.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dapoi.pitjarustest.data.source.ShopRepository
import com.dapoi.pitjarustest.data.source.remote.model.LoginResponse
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    fun login(
        username: String,
        password: String
    ): LiveData<Resource<LoginResponse>> {
        return shopRepository.login(username, password).asLiveData()
    }

    fun insertShop(
        shop: List<StoresItem>
    ) {
        viewModelScope.launch { shopRepository.insertShop(shop) }
    }

    fun getShop(): LiveData<Resource<List<StoresItem>>> {
        return shopRepository.getShop().asLiveData()
    }
}