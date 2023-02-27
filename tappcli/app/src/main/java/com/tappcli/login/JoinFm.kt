package com.tappcli.login;

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.*
import com.google.gson.JsonObject
import com.tappcli.R
import com.tappcli.databinding.LoginJoinFmBinding
import com.tappcli.util.Http
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Int
import kotlin.Throwable
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.toString


class JoinFm : Fragment() {

    val tagName = "[JoinFm]"
    val host = "10.0.2.2:5001" //안드로이드는 127.0.0.1에 접근 불가. 대신 미리 지정된 alias를 사용해 접근가능.
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = LoginJoinFmBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //회원가입 시 형식 유효성 검사를 위한 텍스트 와쳐 생성 및 실행
        validationCheck()


        //회원가입 완료 버튼 클릭시
        binding.joinCompleteBt.setOnClickListener { v ->
            //todo 레트로핏서버로통신 - 회원정보들 json으로 넘김. - 서버에서 디비로 정보insert - 성공유무 정보 반환 - 가입완료하면(토스트띄우고) 로그인화면으로가기
            val infoMap = HashMap<kotlin.String, kotlin.String>()
            infoMap["user_email"] = binding.joinEmailInput.text.toString()
            infoMap["user_name"] = binding.joinNameInput.text.toString()
            infoMap["user_nick"] = binding.joinNicknameInput.text.toString()
            infoMap["user_pwd"] = binding.joinPwInput.text.toString()
//            infoMap["user_pwdc"] = binding.joinPwVerifyInput.text.toString()
            infoMap["user_image"] = ""

            Http.getRetrofitInstance(host)!!.create(Http.HttpLogin::class.java).joinComplete(infoMap)!!
                .enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    Log.e("[JoinFm]", "회원가입 onResponse: 통신성공, code는: " + response.code())
                    if (response.isSuccessful) {
                        val res = response.body()
                        Log.e("[JoinFm]", "회원가입 onResponse: 통신성공, body는: " + res.toString())
                        if (res!!["res"].asBoolean) {
                            Toast.makeText(requireActivity(), "회원가입 하였습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show()
//                            NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)
                            NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_loginFm)
                        } else {
                            Toast.makeText(requireActivity(), "이미 존재하는 회원입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("[JoinFm]", "회원가입 onFailure: " + t.message)
                }
            })
        }


    }

        override fun onResume() {
        super.onResume()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }


    /**
     * 회원가입 시 형식 유효성 검사를 위한 텍스트 와쳐 생성!
     */

    private fun validationCheck() {

        // 필수 입력란 모두 충족되는지 관찰하는 옵져버 생성
        val verifyArray = arrayOf(false, false, false, false, false)
        val verifyForm = MutableLiveData<Array<Boolean>>()
        verifyForm.observe(viewLifecycleOwner) { booleans ->
            var count = 0
            for (aBoolean in booleans) {
                if (aBoolean) {
                    count++
                    binding.joinCompleteBt.isEnabled = count == 5
                }
            }
//            Log.e(tagName, "verifyArray: $verifyArray")
        }


        val emailWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                CoroutineScope(Dispatchers.Main).launch {
                    verifyArray[0] = validateEmail()
                    verifyForm.value = verifyArray

                }
            }
        }
        val nameWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[1] = validateName()
                verifyForm.value = verifyArray
            }
        }
        val nickWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[2] = validateNick()
                verifyForm.value = verifyArray
            }
        }
        val pwWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[3] = validatePw()
                verifyForm.value = verifyArray
            }
        }
        val pwVerifyWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[4] = validatePwVerify()
                verifyForm.value = verifyArray
            }
        }


        binding.joinEmailInput.addTextChangedListener(emailWatcher)
        binding.joinNameInput.addTextChangedListener(nameWatcher)
        binding.joinNicknameInput.addTextChangedListener(nickWatcher)
        binding.joinPwInput.addTextChangedListener(pwWatcher)
        binding.joinPwVerifyInput.addTextChangedListener(pwVerifyWatcher)
    }


    /**
     * 회원가입시 이메일,패스워드 등등의 형식 맞는지 검사 후 textInputLayout 에 오류유무 보여주기
     */
    private suspend fun validateEmail(): Boolean {
        val value = String.valueOf(binding.joinEmailInput.text)
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (value.isEmpty()) {
            binding.joinEmailTextlayout.error = "이메일을 입력해주세요."
            false
        } else if (!value.matches(Regex(emailPattern))) {
            binding.joinEmailTextlayout.error = "이메일 형식이 맞지 않습니다."
            false
        } else {
            //통신해서 중복유무 체크해야함.
            var r = false
            suspendCoroutine { cont: Continuation<Unit> ->

                Http.getRetrofitInstance(host)!!.create(Http.HttpLogin::class.java).isEmailRedundant(value)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        Log.e("[JoinFm]", "isEmailRedundant onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            Log.e("[JoinFm]", "isEmailRedundant onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
                                Toast.makeText(activity, res["res"].asString, Toast.LENGTH_SHORT).show()
//                                NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)
//                                Toast.makeText(activity, "이미 존재하는 Email 입니다.", Toast.LENGTH_SHORT).show()
                                binding.joinEmailTextlayout.error = "사용중인 Email 입니다."
                            } else {
                                Toast.makeText(activity, "사용가능한 Email 입니다.", Toast.LENGTH_SHORT).show()
                                binding.joinEmailTextlayout.error = null
                                r = true
                            }
                        }
                        cont.resumeWith(Result.success(Unit))
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e("[JoinFm]", "isEmailRedundant onFailure: " + t.message)
                    }
                })

            }
            r //비동기 통신 결과 성공시 r은 true를 반환함
        }
    }

    private fun validateName(): Boolean {
        val value = String.valueOf(binding.joinNameInput.text)
        return if (value.isEmpty()) {
            binding.joinNameTextlayout.error = "이름을 입력해주세요."
            false
        } else {
            binding.joinNameTextlayout.error = null
            true
        }
    }

    private fun validateNick(): Boolean {
        val value = String.valueOf(binding.joinNicknameInput.text)
        return if (value.isEmpty()) {
            binding.joinNicknameTextlayout.error = "닉네임을 입력해주세요."
            false
        } else if (value.length > 10) {
            binding.joinNicknameTextlayout.error = "닉네임 길이를 초과하였습니다."
            false
        } else {
            binding.joinNicknameTextlayout.error = null
            true
        }
    }

    private fun validatePw(): Boolean {
        val value = String.valueOf(binding.joinPwInput.text)
        val pwPattern = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$"
        return if (value.isEmpty()) {
            binding.joinPwTextlayout.error = "비밀번호를 입력해주세요."
            false
        } else if (!value.matches(Regex(pwPattern))) {
            binding.joinPwTextlayout.error = "비밀번호 형식이 맞지 않습니다."
            false
        } else {
            binding.joinPwTextlayout.error = null
            true
        }
    }

    private fun validatePwVerify(): Boolean {
        val value = String.valueOf(binding.joinPwInput.text)
        val value2 = String.valueOf(binding.joinPwVerifyInput.text)
        return if (value.isEmpty()) {
            binding.joinPwVerifyTextlayout.error = "비밀번호 확인을 입력해주세요."
            false
        } else if (value != value2) {
            binding.joinPwVerifyTextlayout.error = "비밀번호가 다릅니다."
            false
        } else {
            binding.joinPwVerifyTextlayout.error = null
            true
        }
    }









}
