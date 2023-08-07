package com.ssafy.likloud.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.likloud.MainActivity
import com.ssafy.likloud.R
import com.ssafy.likloud.ui.drawing.DrawingDetailFragmentArgs
import java.util.*

private const val TAG = "MyFirebaseMessageServic_싸피"

class MyFirebaseMessageService : FirebaseMessagingService() {

    // 새로운 토큰이 생성될 때 마다 해당 콜백이 호출된다.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        // 새로운 토큰 수신 시 서버로 전송
        MainActivity.uploadToken(token)
    }

    // Foreground에서 Push Service를 받기 위해 Notification 설정
    // 메시지가 오면 호출되는 함수
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ${remoteMessage.data}")

//        remoteMessage.notification?.apply {
//            Log.d(TAG, "onMessageReceived: ssss")
            val navBuilder = NavDeepLinkBuilder(this@MyFirebaseMessageService)
//            val messageTitle = title 이런 식으로 title body 받을 수 있음
            val pendingIntent : PendingIntent
            when (remoteMessage.data["historyType"]) {
                "GIFT" -> {
                    pendingIntent = navBuilder
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.drawingListFragment)
                        .createPendingIntent()
                }
                // LIKE 혹은 COMMENT 인 경우
                else -> {
                    Log.d(TAG, "onMessageReceived: comment received")
                    val args = DrawingDetailFragmentArgs(remoteMessage.data["drawingId"]!!.toInt())

                    pendingIntent = NavDeepLinkBuilder(this@MyFirebaseMessageService)
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.drawingDetailFragment)
                        .setArguments(args.toBundle())
                        .createPendingIntent()
                }
            }

            // 이하 동일한 코드...

            val builder =
                NotificationCompat.Builder(this@MyFirebaseMessageService, MainActivity.channel_id)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("받았습니다")
                    .setContentText("바디입니다.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setFullScreenIntent(pendingIntent, true)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(101, builder.build())
//        }
    }
}
