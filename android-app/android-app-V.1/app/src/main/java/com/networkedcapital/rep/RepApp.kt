package com.networkedcapital.rep

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.Task
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RepApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ensure FirebaseApp is initialized only if not already initialized
        val firebaseApp = FirebaseApp.getApps(this).firstOrNull() ?: FirebaseApp.initializeApp(this)
        if (firebaseApp == null) {
            Log.e("RepApp", "FirebaseApp initialization failed!")
            return
        }

        // Register for FCM token and send to backend if needed
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
            if (!task.isSuccessful) {
                Log.w("RepApp", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("RepApp", "FCM registration token: $token")
            // TODO: Send this token to your backend if user is logged in
        }
    }
}
