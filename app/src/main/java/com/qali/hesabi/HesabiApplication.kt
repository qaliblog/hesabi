package com.qali.hesabi

import android.app.Application
import com.qali.hesabi.data.AppDatabase

class HesabiApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
