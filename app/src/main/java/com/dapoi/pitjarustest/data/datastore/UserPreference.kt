package com.dapoi.pitjarustest.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val HAS_LOGIN = booleanPreferencesKey("has_login")
private val USERNAME = stringPreferencesKey("username")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preference")

@Singleton
class UserPreference @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveHasLogin(hasLogin: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_LOGIN] = hasLogin
        }
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    val hasLogin = dataStore.data.map { preferences ->
        preferences[HAS_LOGIN] ?: false
    }

    val username = dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }
}