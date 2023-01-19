package com.dapoi.pitjarustest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dapoi.pitjarustest.data.datastore.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val userPreference: UserPreference
) : ViewModel() {

    fun saveHasLogin(hasLogin: Boolean) {
        viewModelScope.launch {
            userPreference.saveHasLogin(hasLogin)
        }
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            userPreference.saveUsername(username)
        }
    }

    val hasLogin = userPreference.hasLogin.asLiveData()
    val username = userPreference.username.asLiveData()
}