package com.softaai.livecoding

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerDataStoreManager(private val context: Context) {

    suspend fun setTimer(timerValue: String){
        context.timerDataStore.edit { preferences ->
            preferences[TIMER_KEY] = timerValue
        }
    }

    val timer : Flow<String>
        get() = context.timerDataStore.data.map { preferences ->
            preferences[TIMER_KEY] ?: "00:00:00"
        }

    companion object {
        private const val DATASTORE_NAME = "timer_preferences"

        private val TIMER_KEY = stringPreferencesKey("timer_key");

        private val Context.timerDataStore by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }
}
