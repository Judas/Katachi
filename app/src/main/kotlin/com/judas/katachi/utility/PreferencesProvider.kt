package com.judas.katachi.utility

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object PreferencesProvider {
    private const val PREFS_NAME = "katachi-prefs"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    fun reader(): SharedPreferences = preferences
    fun writer(): SharedPreferences.Editor = preferences.edit()
}

fun Any.prefReader() = PreferencesProvider.reader()
fun Any.prefWriter() = PreferencesProvider.writer()