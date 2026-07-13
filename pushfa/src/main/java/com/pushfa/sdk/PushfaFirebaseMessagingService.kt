package com.pushfa.sdk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushfaFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Pushfa.handleNewToken(applicationContext, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Pushfa.handleMessage(applicationContext, message)
    }
}
