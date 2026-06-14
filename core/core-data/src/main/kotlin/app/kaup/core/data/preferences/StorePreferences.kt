package app.kaup.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorePreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val STORE_NAME = stringPreferencesKey("store_name")
        val CURRENCY = stringPreferencesKey("currency")
    }

    val storeName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.STORE_NAME]
    }

    val currency: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.CURRENCY]
    }

    suspend fun saveStoreSetup(name: String, currency: String) {
        dataStore.edit { preferences ->
            preferences[Keys.STORE_NAME] = name
            preferences[Keys.CURRENCY] = currency
        }
    }
}
