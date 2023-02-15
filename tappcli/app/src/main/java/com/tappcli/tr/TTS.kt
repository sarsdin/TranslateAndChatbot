package com.tappcli.tr

import android.R.attr.button
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import java.util.*


class TTS(var context: Context) {

    private val tts: TextToSpeech by lazy {
        TextToSpeech(context, OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                val result = tts.setLanguage(Locale.ENGLISH) // Locale.KOREAN
                // 영어로 설정해도 한글을 읽을 수 있고 영어 발음이 한국어로 설정하는것 보다 낫다.
                Log.e("TTS", "Language set English.")
                if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Log.e("TTS", "Language not supported.")
                }
            } else {
                Log.e("TTS", "Initialization failed.")
            }
        })
    }

    fun mainFn(text: String){
        tts.setPitch(1.0f)
        tts.setSpeechRate(1.0f)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uid")

//        btn_Speak = findViewById(R.id.btnSpeak);
//        txtText = findViewById(R.id.txtText);
//        btn_Speak.setOnClickListener(object : View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            fun onClick(v: View?) {
//                val text: String = txtText.getText().toString()
//                tts.setPitch(1.0f)
//                tts.setSpeechRate(1.0f)
//                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uid")
//            }
//        })

    }




}