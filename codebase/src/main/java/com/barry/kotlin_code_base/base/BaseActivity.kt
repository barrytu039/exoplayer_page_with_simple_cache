package com.barry.kotlin_code_base.base

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun <T> lazyExtraData(key : String, default : T) : Lazy<T> {
        return lazy {
            when(default) {
                is String -> intent.getStringExtra(key) as T ?: default
                is Int -> intent.getIntExtra(key, default) as T
                else -> throw IllegalArgumentException("wrong type")
            }
        }
    }

    fun <T> extraData(key : String, default : T) : T {
        return  when(default) {
            is String -> intent.getStringExtra(key) as T ?: default
            is Int -> intent.getIntExtra(key, default) as T
            else -> throw IllegalArgumentException("wrong type")
        }
    }
}