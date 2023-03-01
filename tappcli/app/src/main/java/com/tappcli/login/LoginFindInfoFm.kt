package com.tappcli.login;

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tappcli.R
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tappcli.databinding.LoginFindIdFmBinding
import com.tappcli.databinding.LoginFindInfoFmBinding
import com.tappcli.databinding.LoginJoinFmBinding
import gun0912.tedimagepicker.util.ToastUtil.showToast
import java.util.concurrent.TimeUnit


class LoginFindInfoFm : Fragment() {

    val tagName = "[${this.javaClass.canonicalName}]"
    lateinit var loginVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView

    var mbinding: LoginFindInfoFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = LoginFindInfoFmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.findId.setOnClickListener {
            findNavController().navigate(R.id.action_global_loginFindIdFm)
        }
        binding.findPw.setOnClickListener {
            findNavController().navigate(R.id.action_global_joinFindPwFm)
        }

    }



    override fun onResume() {
        super.onResume()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }








}
