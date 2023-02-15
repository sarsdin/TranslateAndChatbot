package com.tappcli.login;

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.tappcli.R
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tappcli.databinding.LoginJoinFmBinding
import gun0912.tedimagepicker.util.ToastUtil.showToast
import java.util.concurrent.TimeUnit


class JoinFindPwFm : Fragment() {

    val tagName = "[JoinFindPwFm]"
    lateinit var loginVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView
//    lateinit var rva: HomeFmImgRva
//    lateinit var imgVpa: HomeFmImgVpa

    var mbinding: LoginJoinFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }







}
