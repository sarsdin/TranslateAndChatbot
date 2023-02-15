package com.tappcli.chbot

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tappcli.util.Http
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class LoginVm: ViewModel() {

    val tagName = "[ChatBotVm]"
    val gson = GsonBuilder().setPrettyPrinting().create()
    val host = "127.0.0.1:5001"

    var chatL = JsonArray() //채팅+쓴사람 목록 정보
    var liveChatL =  MutableLiveData<JsonArray>()


    init {
//        liveCurrentTranslateMode.value = currentTranslateMode

    }














}
