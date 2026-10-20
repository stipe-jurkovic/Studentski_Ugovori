package com.ugovori.studentskiugovori.utils

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object PreferenceHelper {

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    operator fun SharedPreferences.set(key: SPKey, value: Any?) {
        val keyStr = key.toString()
        when (value) {
            is String? -> edit { it.putString(keyStr, value) }
            is Int -> edit { it.putInt(keyStr, value) }
            is Boolean -> edit { it.putBoolean(keyStr, value) }
            is Float -> edit { it.putFloat(keyStr, value) }
            is Long -> edit { it.putLong(keyStr, value) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    inline operator fun <reified T> SharedPreferences.get(
        key: SPKey,
        defaultValue: T? = null
    ): T {
        val keyStr = key.toString()
        return when (T::class) {
            String::class -> getString(keyStr, defaultValue as? String ?: "") as T
            Int::class -> getInt(keyStr, defaultValue as? Int ?: -1) as T
            Boolean::class -> getBoolean(keyStr, defaultValue as? Boolean == true) as T
            Float::class -> getFloat(keyStr, defaultValue as? Float ?: -1f) as T
            Long::class -> getLong(keyStr, defaultValue as? Long ?: -1) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    fun SharedPreferences.booleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> = callbackFlow {
        trySend(getBoolean(key, defaultValue))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getBoolean(key, defaultValue))
            }
        }

        try {
            registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                try {
                    unregisterOnSharedPreferenceChangeListener(listener)
                } catch (_: Exception) {}
            }
        } catch (_: UnsupportedOperationException) {
            awaitClose {}
        }
    }
}
