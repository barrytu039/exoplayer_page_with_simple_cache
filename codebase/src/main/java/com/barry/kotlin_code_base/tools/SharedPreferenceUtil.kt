package com.barry.kotlin_code_base.tools

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtil(val context: Context, val name : String) {

    private val sharedPreferenceName : String

    init {
        sharedPreferenceName = name
    }

    private fun getSharedPreference() : SharedPreferences {
        return context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
    }

    fun setString(key : String, value : String) {
        getSharedPreference().edit().putString(key,value).apply()
    }

    fun setStringSync(key : String, value : String) {
        getSharedPreference().edit().putString(key,value).commit()
    }

    fun getString(key: String, defValue : String) : String? {
        return getSharedPreference().getString(key, defValue)
    }

    fun setBoolean(key : String, value : Boolean) {
        getSharedPreference().edit().putBoolean(key,value).apply()
    }

    fun setBooleanSync(key : String, value : Boolean) {
        getSharedPreference().edit().putBoolean(key,value).commit()
    }

    fun getBoolean(key: String, defValue : Boolean) : Boolean {
        return getSharedPreference().getBoolean(key, defValue)
    }

    fun setInt(key : String, value : Int) {
        getSharedPreference().edit().putInt(key,value).apply()
    }

    fun setIntSync(key : String, value : Int) {
        getSharedPreference().edit().putInt(key,value).commit()
    }

    fun getInt(key: String, defValue : Int) : Int {
        return getSharedPreference().getInt(key, defValue)
    }

    fun clearData(key : String) {
        getSharedPreference().edit().remove(key).apply()
    }

    fun deleteAllData() {
        getSharedPreference().edit().clear().commit()
    }

}