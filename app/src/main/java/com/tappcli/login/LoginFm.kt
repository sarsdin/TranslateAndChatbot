package com.tappcli.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tappcli.R
import com.tappcli.chbot.LoginVm
import com.tappcli.databinding.LoginFmBinding
import java.lang.String
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Int

class LoginFm : Fragment() {

    val tagName = "[LoginFm]"
    lateinit var loginVm: LoginVm

    var mbinding: LoginFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = LoginFmBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뒤로가기 버튼 클릭시 뒤로
        binding.toolbarSlide.setOnClickListener {
            findNavController().navigateUp()
        }

        // 로그인 할때 형식 검사
        val verifyArray = arrayOf(false, false)
        val verifyForm = MutableLiveData<Array<Boolean>>()
        verifyForm.observe(viewLifecycleOwner, Observer {
            binding.loginLoginBt.isEnabled = it.component1() && it.component2()
        })
        val emailWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[0] = validateEmail()
                verifyForm.value = verifyArray
            }
        }
        val pwWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[1] = validatePw()
                verifyForm.value = verifyArray
            }
        }

        binding.loginMainEmailInput.addTextChangedListener(emailWatcher)
        binding.loginMainPwdInput.addTextChangedListener(pwWatcher)


        //회원가입 버튼 클릭
        binding.loginJoinBt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFm_to_joinAuthFm)
        }

        //비밀번호 찾기 버튼 클릭
        binding.loginFindpwBt.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFm_to_)
        }


    }





    override fun onResume() {
        super.onResume()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }


    private fun validateEmail(): Boolean {
        val value = String.valueOf(binding.loginMainEmailInput.text)
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (value.isEmpty()) {
            //            binding.editId.setError("이메일을 입력해주세요.");
            binding.loginMainEmailLayout.error = "이메일을 입력해주세요."
            false
        } else if (!value.matches(Regex(emailPattern))) {
            //            binding.editId.setError("이메일 형식이 맞지 않습니다.");
            binding.loginMainEmailLayout.error = "이메일 형식이 맞지 않습니다."
            false
        } else {
            //            binding.editId.setError(null);
            binding.loginMainEmailLayout.error = null
            true
        }
    }

    private fun validatePw(): Boolean {
        val value = String.valueOf(binding.loginMainPwdInput.text)
        val pwPattern = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$"
        return if (value.isEmpty()) {
            //            binding.editPwd.setError("비밀번호를 입력해주세요.");
            binding.loginMainPwdLayout.error = "비밀번호를 입력해주세요."
            false
        } else if (!value.matches(Regex(pwPattern))) {
            //            binding.editPwd.setError("비밀번호 형식이 맞지 않습니다.");
            binding.loginMainPwdLayout.error = "비밀번호 형식이 맞지 않습니다."
            false
        } else {
            //            binding.editPwd.setError(null);
            binding.loginMainPwdLayout.error = null
            true
        }
    }


}