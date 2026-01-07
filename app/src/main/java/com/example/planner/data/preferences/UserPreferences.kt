package com.example.planner.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LOGGED_IN_USER_ID = longPreferencesKey("logged_in_user_id")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val REMEMBER_USER = booleanPreferencesKey("remember_user")
    }

    val loggedInUserId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[LOGGED_IN_USER_ID]
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_LAUNCH] ?: true
    }

    val rememberUser: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[REMEMBER_USER] ?: false
    }

    suspend fun setLoggedInUser(userId: Long, remember: Boolean = false) {
        dataStore.edit { preferences ->
            preferences[LOGGED_IN_USER_ID] = userId
            preferences[REMEMBER_USER] = remember
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_USER_ID)
            if (preferences[REMEMBER_USER] != true) {
                preferences.remove(REMEMBER_USER)
            }
        }
    }

    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_USER_ID)
        }
    }
}
