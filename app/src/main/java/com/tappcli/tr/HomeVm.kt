package com.tappcli.tr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class HomeVm : ViewModel() {

    val tagName = "[HomeVm]"
    val gson = GsonBuilder().setPrettyPrinting().create()

    //현재 번역 모드 변수
    var currentTranslateMode = "한영" //기본값 한영으로 설정. <> 영한
    var liveCurrentTranslateMode = MutableLiveData<String>()

    //번역 버튼의 값
    var firstBt = "한국어"
    var liveFirstBt = MutableLiveData<String>()
    var secondBt = "영어"
    var liveSecondBt = MutableLiveData<String>()


    init {
        liveCurrentTranslateMode.value = currentTranslateMode
    }
















}