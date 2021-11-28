package com.example.myinfo.utils.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStorePrefs(context: Context) {

    private val Context.dataStore by preferencesDataStore("user_prefs")
    private val mDataStore: DataStore<Preferences> = context.dataStore

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("USER_NAME")
        val USER_AGE_KEY = stringPreferencesKey("USER_AGE")
        val USER_NEVER_SHOW_AGAIN = booleanPreferencesKey("NEVER_SHOW_AGAIN")
        val ANIMATE_INTRO_TEXT = intPreferencesKey("INTRO_TEXT_FLAG")
    }

    suspend fun storeUserData(name: String, age: String, neverShow: Boolean) {
        mDataStore.edit { preferences ->

            preferences[USER_NAME_KEY] = name
            preferences[USER_AGE_KEY] = age
            preferences[USER_NEVER_SHOW_AGAIN] = neverShow

        }
    }

    suspend fun setAnimateTextFlag(setFlag : Int) {
        mDataStore.edit { preference ->
            preference[ANIMATE_INTRO_TEXT] = setFlag
        }
    }

    suspend fun deleteAllPreferences() {
        mDataStore.edit {
            it.clear()
        }
    }

    /**
     * Retrieving methods
     */

    val getAnimateTextFlag: Flow<Int> = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val animateFlag = preferences[ANIMATE_INTRO_TEXT] ?: 0
            animateFlag
        }


    val userNameFlow: Flow<String> = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userName = preferences[USER_NAME_KEY] ?: "XXX"
            userName
        }

    val userAgeFlow: Flow<String> = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userAge = preferences[USER_AGE_KEY] ?: "n/a"
            userAge
        }

    val userNeverShowAgain: Flow<Boolean> = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val neverShow = preferences[USER_NEVER_SHOW_AGAIN] ?: false
            neverShow
        }


}