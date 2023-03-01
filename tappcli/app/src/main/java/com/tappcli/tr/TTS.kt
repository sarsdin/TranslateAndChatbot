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

    lateinit var langCode: String

    private val tts: TextToSpeech by lazy {
        TextToSpeech(context, OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                val result = tts.setLanguage(언어설정(langCode)) // Locale.KOREAN
                if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Log.e("TTS", "Language not supported.")
                }
            } else {
                Log.e("TTS", "Initialization failed.")
            }
        })
    }
    fun 언어설정(langCode:String):Locale{
        Log.e("TTS", "Language set ${langCode}.")
        val res = when(langCode){
          "ko" -> {Locale.KOREAN}
          "en" -> {Locale.ENGLISH}
          "fr" -> {Locale.FRENCH}
          "de" -> {Locale.GERMAN}
          "zh" -> {Locale.CHINESE}
          "ja" -> {Locale.JAPANESE}
          "es" -> {Locale("es")}
          else -> {Locale.ENGLISH}
        }
        return res
    }

    fun mainFn(text: String, langCode: String){
        this.langCode = langCode
        tts.setPitch(1.0f)
        tts.setSpeechRate(1.0f)
        tts.setLanguage(언어설정(langCode))
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