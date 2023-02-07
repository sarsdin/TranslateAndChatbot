package com.tappcli.tr
import android.util.Log

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.StrictMode
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.tappcli.MyApp
import com.tappcli.R
import com.tappcli.databinding.ResultFmBinding
import java.io.IOException

class GoogleTranslationApi (var activity: FragmentActivity, var binding : ResultFmBinding){

    private var tagName = "GoogleTranslationApi"
    private var translate: Translate? = null


    fun getTranslateService() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            //try catch resource 구문과 같은 use 문 - 여기선 inputstream을 가져와 use 블럭문에서 인자로 사용함.
            activity.resources.openRawResource(R.raw.credentials).use { `is` ->
                val myCredentials = GoogleCredentials.fromStream(`is`)
                val translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

    }


    fun translate(타겟언어코드:String) {
        //번역 모드에 따른 언어변수설정
        var targetLang = "en"
        if(타겟언어코드 != ""){
            targetLang = 타겟언어코드
        }else{
            targetLang = "en"
        }

        Log.e(tagName, "타겟언어코드: ${타겟언어코드}")

        //Get input text to be translated:
        val originalText: String = binding.et.text.toString()
        val translation = translate!!.translate(
            originalText,
            Translate.TranslateOption.targetLanguage(타겟언어코드), //tr = 터키어 , jp = 일본어 등..
            Translate.TranslateOption.model("base")
        )

        //Translated text and original text are set to TextViews:
        binding.resultTv.text = translation.translatedText

    }

//    private fun checkInternetConnection(): Boolean {
//
//        //Check internet connection:
//        val connectivityManager = .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
//
//        //Means that we are connected to a network (mobile or wi-fi)
//        return activeNetwork?.isConnected == true
//
//    }




}