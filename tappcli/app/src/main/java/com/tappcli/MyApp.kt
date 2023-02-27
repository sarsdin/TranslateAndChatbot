package com.tappcli

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
//import android.preference.PreferenceManager
import androidx.preference.PreferenceManager
import com.google.firebase.auth.UserInfo
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*

class MyApp : Application() {
//    var application: Application? = null

    init {
        application = this
    }
//토스트 라이브러리 옵션 설정 - 선택사항임 - 앱클래스파일 어디서든 이명령을 통해 설정 가능
//        Toasty.Config.getInstance()
//            .tintIcon(boolean tintIcon) // optional (apply textColor also to the icon)
//            .setToastTypeface(@NonNull Typeface typeface) // optional
//            .setTextSize(14) // optional
//            .allowQueue(true) // optional (prevents several Toastys from queuing)
//            .setGravity(boolean isRTL, int xOffset, int yOffset) // optional (set toast gravity, offsets are optional)
//            .supportDarkTheme(boolean isRTL) // optional (whether to support dark theme or not)
//            .setRTL(boolean isRTL) // optional (icon is on the right)
//            .apply(); // required
//        Toasty.Config.reset(); // 앱클래스파일 어디서든 이명령을 통해 초기화 가능

    companion object {
        lateinit var application: MyApp
        var id = 0
        var email = ""
        var nick = ""
        var liveLoginState = MutableLiveData<Int>().also { it.value = 0 }

        fun logOut(): Boolean {
            id = 0
            email = ""
            nick = ""
            liveLoginState.value = id
            return true
        }

        @JvmName("getApplication1")
        fun getApplication(): MyApp {
            return application
        }

        //fromFlag const
        val 히스토리_및_즐겨찾기에서_왔음:Int = 1



        //    public static boolean isnaver = false;
        var sp: SharedPreferences? = null
        private const val TAG = "MyApp"

        //채팅방 안에 있는지 확인여부 - GroupChatinnerfm 안에 있으면 true 그외는 false 처리해야함 - 변경: 방번호로 바꿈. 서비스에서
        // 방번호에 있는지 확인해서 해당하는 방번호가 일치하면 알림을 보내지않고, 일치하지 않으면 그방에 없는 것이니 보내야함!
        var inChatRoom = 0

        //객체를 깊은 복사하는 메소드 - 데이터주소까지 완전히 다른 객체가 된다
        @Throws(IOException::class, ClassNotFoundException::class)
        fun deepCopy(o: Any?): Any {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(o)

            val buff = baos.toByteArray()
            val bais = ByteArrayInputStream(buff)
            val os = ObjectInputStream(bais)
            return os.readObject()
        }

        val defaultSp: SharedPreferences
            get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(application!!.applicationContext)

        @JvmName("getSp1")
        fun getSp(): SharedPreferences? {
            sp = application!!.getSharedPreferences("emailcode", MODE_PRIVATE)
            return sp
        }

        @JvmName("setSp1")
        fun setSp(sp: SharedPreferences?) {
            Companion.sp = sp
        }

        fun getTime(ui표시orData: String, datetime: String?): String {
            var format = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss") // H(0-23) hh(1-12)
            var out_format = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
            var out_format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
            var out_format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            if (ui표시orData == "ui") {
                val ldt = LocalDateTime.parse(datetime, format) //포멧에 맞는 형태의 문자열 날짜시간값을 받아와서 파싱함
//            LocalDateTime.now().toInstant();
//            ldt.toEpochSecond(ZoneOffset.UTC);
                val res_st = ldt.format(out_format)
                val res_st2 = ldt.format(out_format2)
                val res_st3 = ldt.format(out_format3)
                val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) /*atZone(ZoneId.systemDefault()).toEpochSecond()*/
                //zoneoffset의 구분은 중요하다. systemdefault zone으로 정하면 서울 시간을 기준으로 계산되고 utc기준이랑은 시차가 생기게 되니 주의해야한다.
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow) //1644034298
                val mDate_muter = mDate.toInstant().epochSecond
                val second =
                    currentTime - ldt.toEpochSecond(ZoneOffset.UTC) + 2 //시간이 -가 되는 증세가 있음. 기기마다 시간계산이 미묘하게 달라서..+2초해줌
                val minute = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60L
                val hour = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60
                val day = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24
                val year = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24 / 365
//            Log.e("MyApp", "res_st: "+res_st );
//            Log.e("MyApp", "res_st2: "+res_st2 );
//            Log.e("MyApp", "res_st3: "+res_st3 );
//            Log.e("MyApp", "currentTime: "+currentTime );
//            Log.e("MyApp", "ldt.toEpochSecond(ZoneOffset.UTC): "+ldt.toEpochSecond(ZoneOffset.UTC) );
//            Log.e("MyApp", "현재시간 - 저장된 시간 (초): "+ second );
//            Log.e("MyApp", "현재시간 - 저장된 시간 (분): "+ minute);
//            Log.e("MyApp", "현재시간 - 저장된 시간 (시간): "+ hour);
//            Log.e("MyApp", "현재시간 - 저장된 시간 (일): "+ day );
//            Log.e("MyApp", "현재시간 - 저장된 시간 (년): "+ year );
                val res = ""
                //각 시간 기준을 못넘기면 그 이전 기준으로 계산된 시간을 리턴해줌
                if (minute < 1) {
                    return second.toString() + "초전"
                } else if (hour < 1) {
                    return minute.toString() + "분전"
                } else if (day < 1) {
                    return hour.toString() + "시간전"
                } else if (year < 1) {
                    return day.toString() + "일전"
                }
                return res_st

            } else if (ui표시orData == "data") { //현재 시간을 "yyyy-MM-dd hh:mm:ss" 형태의 포맷으로 리턴해줌
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow)
//            mDate.toInstant().getEpochSecond();
                val mtime = SimpleDateFormat()
                return SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(mDate)

            } else if (ui표시orData == ".") { //현재 시간을 "yyyy-MM-dd hh:mm:ss" 형태의 포맷으로 리턴해줌
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow)
//            mDate.toInstant().getEpochSecond();
                val mtime = SimpleDateFormat()
                return SimpleDateFormat("yyyy.MM.dd").format(mDate)

            } else if (ui표시orData == ".ui") {
                format = DateTimeFormatter.ISO_LOCAL_DATE_TIME // H(0-23) hh(1-12)
                out_format = DateTimeFormatter.ofPattern("yyyy.MM.dd H:mm")
                val ldt = LocalDateTime.parse(datetime, format) //포멧에 맞는 형태의 문자열 날짜시간값을 받아와서 파싱함
                return ldt.format(out_format)

            } else if (ui표시orData == "ui2") {
                val ldt = LocalDateTime.parse(datetime, format) //포멧에 맞는 형태의 문자열 날짜시간값을 받아와서 파싱함
                val res_st = ldt.format(out_format)
                val res_st2 = ldt.format(out_format2)
                val res_st3 = ldt.format(out_format3)
                val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) /*atZone(ZoneId.systemDefault()).toEpochSecond()*/
                //zoneoffset의 구분은 중요하다. systemdefault zone으로 정하면 서울 시간을 기준으로 계산되고 utc기준이랑은 시차가 생기게 되니 주의해야한다.
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow) //1644034298
                val mDate_muter = mDate.toInstant().epochSecond
                val second =
                    currentTime - ldt.toEpochSecond(ZoneOffset.UTC) + 2 //시간이 -가 되는 증세가 있음. 기기마다 시간계산이 미묘하게 달라서..+2초해줌
                val minute = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60L
                val hour = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60
                val day = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24
                val year = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24 / 365
                val res = ""
                //각 시간 기준을 못넘기면 그 이전 기준으로 계산된 시간을 리턴해줌
                if (minute < 1) {
                    return second.toString() + "초 경과"
                } else if (hour < 1) {
                    return minute.toString() + "분 경과"
                } else if (day < 1) {
                    return hour.toString() + "시간 경과"
                } else if (year < 1) {
                    return day.toString() + "일 경과"
                }
                return res_st

            } else if (ui표시orData == "ui3") {
                val ldt = LocalDateTime.parse(datetime, format) //포멧에 맞는 형태의 문자열 날짜시간값을 받아와서 파싱함
                val res_st = ldt.format(out_format)
                val res_st2 = ldt.format(out_format2)
                val res_st3 = ldt.format(out_format3)
                val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) /*atZone(ZoneId.systemDefault()).toEpochSecond()*/
                //zoneoffset의 구분은 중요하다. systemdefault zone으로 정하면 서울 시간을 기준으로 계산되고 utc기준이랑은 시차가 생기게 되니 주의해야한다.
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow) //1644034298
                val mDate_muter = mDate.toInstant().epochSecond
                val second =
                    ldt.toEpochSecond(ZoneOffset.UTC) + 2 - currentTime //시간이 -가 되는 증세가 있음. 기기마다 시간계산이 미묘하게 달라서..+2초해줌
                val minute = (ldt.toEpochSecond(ZoneOffset.UTC) - currentTime) / 60L
                val hour = (ldt.toEpochSecond(ZoneOffset.UTC) - currentTime) / 60 / 60
                val day = (ldt.toEpochSecond(ZoneOffset.UTC) - currentTime) / 60 / 60 / 24
                val year = (ldt.toEpochSecond(ZoneOffset.UTC) - currentTime) / 60 / 60 / 24 / 365
                val res = ""
                //각 시간 기준을 못넘기면 그 이전 기준으로 계산된 시간을 리턴해줌
                if (minute < 1) {
                    return second.toString() + "초"
                } else if (hour < 1) {
                    return minute.toString() + "분"
                } else if (day < 1) {
                    return hour.toString() + "시간"
                } else if (year < 1) {
                    return day.toString() + "일"
                }
                return res_st

            } else if (ui표시orData == "ui4") {
                val ldt = LocalDateTime.parse(datetime, format) //포멧에 맞는 형태의 문자열 날짜시간값을 받아와서 파싱함
                val res_st = ldt.format(out_format)
                val res_st2 = ldt.format(out_format2)
                val res_st3 = ldt.format(out_format3)
                val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) /*atZone(ZoneId.systemDefault()).toEpochSecond()*/
                //zoneoffset의 구분은 중요하다. systemdefault zone으로 정하면 서울 시간을 기준으로 계산되고 utc기준이랑은 시차가 생기게 되니 주의해야한다.
                val mNow = System.currentTimeMillis()
                val mDate = Date(mNow) //1644034298
                val mDate_muter = mDate.toInstant().epochSecond
                val second =
                    currentTime - ldt.toEpochSecond(ZoneOffset.UTC) + 2 //시간이 -가 되는 증세가 있음. 기기마다 시간계산이 미묘하게 달라서..+2초해줌
                val minute = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60L
                val hour = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60
                val day = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24
                val year = (currentTime - ldt.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24 / 365
                val res = ""
                //각 시간 기준을 못넘기면 그 이전 기준으로 계산된 시간을 리턴해줌
                if (minute < 1) {
                    return "방금전"
                } else if (hour < 1) {
                    return minute.toString() + "분전"
                } else if (day < 1) {
                    return hour.toString() + "시간전"
                } else if (year < 1) {
                    return day.toString() + "일전"
                }
                return res_st
            }

            return ""
        }
    }










}
