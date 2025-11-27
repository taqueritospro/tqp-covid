package com.covid.tqp.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val LAST_VIEWED_COUNTRY = stringPreferencesKey("last_viewed_country")
    }

    val lastViewedCountry: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_VIEWED_COUNTRY]
        }

    suspend fun saveLastViewedCountry(countryName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_VIEWED_COUNTRY] = countryName
        }
    }
}