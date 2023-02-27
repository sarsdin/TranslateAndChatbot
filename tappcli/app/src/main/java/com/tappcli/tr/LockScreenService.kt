package com.tappcli.tr

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.tappcli.MainActivity
import com.tappcli.R

class LockScreenService : Service() {

    // 이 서비스가 동작하는 한 인텐트를 리시브 받고 있으며, 화면꺼짐 인텐트필터가 발생시 mainActivity 재실행하게함. 잠금화면에 띄우기 위해서.
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                when(intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        val newIntent = Intent(context, /*LockScreenActivity*/MainActivity::class.java)
                        //서비스에서 액티비티를 실행하게하기 위해서는 밑의 flag 가 필수.
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        startActivity(newIntent)
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private final val ALARM_ID = "com.tappcli.lockscreen"

    override fun onCreate() {
        super.onCreate()

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(ALARM_ID, "잠금화면 테스트", NotificationManager.IMPORTANCE_DEFAULT)

        nm.createNotificationChannel(channel)

        //알림바확장에서 포그라운드서비스 상태인 알림창을 클릭하면 번역앱 메인액티비티가 실행되도록 pending intent 구성.
        val pending = PendingIntent
            .getActivity(this, 0, Intent(this, MainActivity::class.java),
//                PendingIntent.FLAG_CANCEL_CURRENT
                PendingIntent.FLAG_MUTABLE
            )

        val notification = Notification.Builder(this, ALARM_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("번역 잠금화면 서비스")
            .setContentText("번역 잠금화면 서비스 동작중")
            .setContentIntent(pending)
            .build()

        //위에서 만든 알림 객체를 이용해 알림 포그라운드 서비스를 계속해서 띄움(실행). 이때 알림 클릭시 위에서 등록한 pending intent 가 작동함.
        //이 매소드는 지속적으로 알림서비스를 제공한다는 것에 의의가 있다. 서비스가 실행중이지 않은데 이것을 실행한다고 서비스가 실행되는건 아님.
        //이거 호출전에 startService()등으로 먼저 서비스를 실행하고 호출해야 지속 효과가 있음.
        startForeground(1, notification)
    }

    //서비스 시작시 실행(MainActivity:startForegroundService(intent)) - 필터만들어서 위에서 만든 브로드캐스트리시버에 추가하고 시스템에 등록함.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        //위에서 만든 브로드캐스트리시버와 필터를 시스템에 등록.
        registerReceiver(receiver, filter)

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        //서비스가 종료될때는 등록한 브로드캐스트 리시버도 등록해제
        unregisterReceiver(receiver)
    }
}