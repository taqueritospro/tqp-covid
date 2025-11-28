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

/**
 * Extensión para [Context] que proporciona una instancia singleton de [DataStore].
 *
 * El nombre del DataStore es "user_preferences".
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repositorio para gestionar las preferencias del usuario utilizando Jetpack DataStore.
 *
 * Esta clase proporciona una forma segura y asíncrona de almacenar y recuperar datos simples,
 * como el último país consultado. Es un Singleton gestionado por Hilt.
 *
 * @param context El contexto de la aplicación, inyectado por Hilt.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    /**
     * Contiene las claves utilizadas para almacenar valores en [DataStore].
     */
    private object PreferencesKeys {
        val LAST_VIEWED_COUNTRY = stringPreferencesKey("last_viewed_country")
    }

    /**
     * Un [Flow] que emite el último nombre de país consultado por el usuario.
     *
     * Se actualiza automáticamente cada vez que el valor cambia en el DataStore.
     * Emite `null` si no se ha guardado ningún país.
     */
    val lastViewedCountry: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_VIEWED_COUNTRY]
        }

    /**
     * Guarda el nombre del último país consultado en el DataStore.
     *
     * Esta es una función `suspend` y debe ser llamada desde una corutina.
     *
     * @param countryName El nombre del país a guardar.
     */
    suspend fun saveLastViewedCountry(countryName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_VIEWED_COUNTRY] = countryName
        }
    }
}