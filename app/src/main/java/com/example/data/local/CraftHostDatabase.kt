package com.example.data.local

import android.content.Context

/**
 * Convenience helper delegating to AppDatabase.
 */
object CraftHostDatabase {
    fun getDatabase(context: Context): AppDatabase = AppDatabase.getDatabase(context)
}
