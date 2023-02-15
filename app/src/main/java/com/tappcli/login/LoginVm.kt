package com.tappcli.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginVm : ViewModel() {

    var email  = ""
    var name  = ""
    var nick  = ""
    var tel  = ""

    var phoneAuthNum: String = ""
    var isResendPhoneAuth: Boolean = false
    val etPhoneNum = MutableLiveData<String>("")
    val etAuthNum = MutableLiveData<String>("")

//    private val _requestPhoneAuth = MutableLiveData<Boolean>()
//    private val _requestResendPhoneAuth = MutableLiveData<Boolean>()
//    val requestPhoneAuth: LiveData<Boolean> get() = _requestPhoneAuth
//    val requestResendPhoneAuth: LiveData<Boolean> get() = _requestResendPhoneAuth
//
//    init{
//        requestPhoneAuth()
//    }
//
//    fun requestPhoneAuth() {
//        if (!isResendPhoneAuth) { //첫 폰인증
//            _requestPhoneAuth.value = !etPhoneNum.value.isNullOrBlank()
//        } else { //재시도
//            _requestResendPhoneAuth.value = !etPhoneNum.value.isNullOrBlank()
//        }
//    }




}