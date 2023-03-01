package com.tappcli.login;

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.*
import com.google.gson.JsonObject
import com.tappcli.MyApp
import com.tappcli.MyApp.Companion.id
import com.tappcli.R
import com.tappcli.databinding.LoginFindPwFmBinding
import com.tappcli.util.Http
import gun0912.tedimagepicker.util.ToastUtil.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine


class LoginFindPwFm : Fragment() {

    val tagName = "[${this.javaClass.canonicalName}]"
    lateinit var loginVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView

    var mbinding: LoginFindPwFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = LoginFindPwFmBinding.inflate(inflater, container, false)
        return binding.root
    }


/*
    전체적인 프로세스

    이름과 이메일 입력 형식 검사되면 인증번호 발송 버튼 활성화
    findpw_name_textlayout,
    findpw_name_input,
    findpw_email_input,
    findpw_email_textlayout
    findpw_email_send_bt
    발송 클릭시 서버에서 해당하는 이름에 이메일이 있는지 확인. 없으면 false와 함께 없다고 메시지 띄움.

    있으면 true와 함께 이메일로 인증번호 보내고 이메일번호테이블에 저장. - 이메일 발송 번호저장용 테이블 필요. 이름,이메일,발송시간,만료시간,발송번호
    그리고, 인증번호 창 보이게 함. 시간 카운트 시작. 시간이 다되면 번호확인 버튼 비활성화. 인증번호입력창도 비활성화. 메시지+
    findpw_email_confirm_input,
    findpw_email_confirm_textlayout,
    findpw_email_verify_number_tv,
    findpw_email_verify_bt

    번호확인 누르면 서버로 인증번호창에 입력된 번호를 보내서 번호 인증.
    서버에서 디비에 확인후 맞으면 테이블에서 그 레코드를 지우고, 인증됐다는 true를 클라에 보냄.
    클라에서는 true를 받으면 이름,이메일, 인증번호 입력창 모두 gone 시키고, 새비밀번호 입력창그룹을 보여줌.
    findpw_name_email_input_group
    findpw_email_verify_group
    input_new_pw_group

    새비밀번호 입력창에서 입력검증한후 비번이 맞으면 비밀번호 변경완료 버튼을 활성화 시킴.
    그거 누르면 서버디비로 비번 업데이트 통신작업
    findpw_pw_verify_input,
    findpw_pw_verify_textlayout,
    findpw_pw_input, findpw_pw_textlayout,
    findpw_complete_bt

*/




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationCheck() //사용자 입력 정보 형식 검증. 모두 맞으면 번호발송 버튼 활성화
        validationCheckForNewPw() //새 비밀번호 입력 형식 검증. 모두 맞으면 비밀번호 변경 버튼 활성화


        // up button 클릭시
        binding.toolbarSlide.setOnClickListener {
            //로그인 페이지까지 팝하면서 날라감.
//            findNavController().navigate(R.id.action_global_loginFm, Bundle.EMPTY,
//                NavOptions.Builder().setPopUpTo(R.id.action_global_loginFm, false).build()
//            )
            findNavController().navigateUp()
        }



        //이메일 번호 발송 버튼 클릭시
        binding.findpwEmailSendBt.setOnClickListener {
            //서버통신
            CoroutineScope(Dispatchers.IO).launch {
                emailVnumReq()

            }
        }


        // 이메일 인증번호 발송 통신결과에 따른 ui 변경 - 인증번호 입력 그룹
        state = MutableLiveData<Boolean>().apply {
            observe(viewLifecycleOwner){
                if(it){
                    showToast("인증번호가 발송되었습니다.")
                    binding.findpwEmailVerifyGroup.setVisibility(View.VISIBLE)
                    binding.findpwEmailVerifyGroup.isEnabled = true
                    binding.findpwEmailConfirmInput.text = null
                    //인증 시간 카운트 시작. 다되면 번호확인 버튼 비활성화
                    binding.findpwEmailVerifyNumberTv.start(90000) //1분30초
                    MyApp.hideKeyboard(requireActivity())
                    binding.findpwEmailConfirmInput.requestFocus()

                }else{
                    showToast("위 정보에 해당하는 사용자가 없습니다.")
                    binding.findpwEmailVerifyGroup.setVisibility(View.GONE)
                    binding.findpwEmailVerifyGroup.isEnabled = false
                    binding.findpwEmailVerifyNumberTv.setDefault()
                }
            }
        }

        // TimerView 인증시간 유무에 따른 ui 표시변경
        binding.findpwEmailVerifyNumberTv.mCertification.observe(viewLifecycleOwner){
            if(it){
                binding.findpwEmailConfirmTextlayout.error = null
                //인증번호 입력 그룹 활성
                binding.findpwEmailConfirmTextlayout.isEnabled = true
                binding.findpwEmailVerifyBt.isEnabled = true

            }else{
                binding.findpwEmailConfirmTextlayout.error = "인증시간이 만료. 재발송해주세요."
                //인증번호 입력 그룹 비활성
                binding.findpwEmailConfirmTextlayout.isEnabled = false
                binding.findpwEmailVerifyBt.isEnabled = false

            }
        }


        //번호확인 버튼 클릭시 - 서버통신 디비 EmailNumber 테이블에서 번호비교
        binding.findpwEmailVerifyBt.setOnClickListener {
            binding.findpwEmailConfirmInput.text
            CoroutineScope(Dispatchers.IO).launch {
                val res = 번호확인인증()
                Handler(Looper.getMainLooper()).post {
                    if(res){
                        //번호 맞을시 새 비밀번호 입력창 띄움
                        showToast("인증번호 확인")
                        binding.findpwEmailConfirmTextlayout.error = null
                        binding.findpwNameEmailInputGroup.visibility = View.GONE
                        binding.findpwEmailVerifyGroup.visibility = View.GONE
                        binding.inputNewPwGroup.visibility = View.VISIBLE
                        binding.findpwCompleteBt.visibility = View.VISIBLE
                    } else {
                        binding.findpwEmailConfirmTextlayout.error = "유효한 인증번호가 필요."
                    }
                }
            }

        }




        //비밀번호 변경 버튼 클릭시 - 서버통신
        binding.findpwCompleteBt.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                새비밀번호요청()
            }
        }


    }




    lateinit var state : MutableLiveData<Boolean> //인증번호 요청 통신결과에 따라 ui 조정
    private suspend fun emailVnumReq(): Boolean {
        var result = true
        val value = mapOf(
            Pair("user_name", binding.findpwNameInput.text.toString()),
            Pair("user_email", binding.findpwEmailInput.text.toString())
        )

        suspendCoroutine { cont: Continuation<Unit> ->

            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.HttpLogin::class.java).emailVnumReq(value)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
//                        Log.e(tagName, "isEmailRedundant onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
//                            Log.e(tagName, "isEmailRedundant onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, res["res"].asString, Toast.LENGTH_SHORT).show()
//                                NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)
                                state.value = true


                            } else {
                                Toast.makeText(activity, "이메일 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                state.value = false
                                result = false
                            }
                        }
                        cont.resumeWith(Result.success(Unit))
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e(tagName, "isEmailRedundant onFailure: " + t.message)
                    }
                })
        }
        return result //비동기 통신 결과 성공시 r은 true를 반환함
    }




    private suspend fun 번호확인인증(): Boolean {
        val methodName = Thread.currentThread().stackTrace[1].methodName //현재 메소드 이름 얻기
        var result = true
        val value = mapOf(
            Pair("vnum", binding.findpwEmailConfirmInput.text.toString()),
            Pair("user_email", binding.findpwEmailInput.text.toString())
        )

        suspendCoroutine { cont: Continuation<Unit> ->

            //이곳의 서버요청시 인증됐다면 true를 받고, 서버에서는 그 인증번호 레코드를 테이블에서 지워야함.
            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.HttpLogin::class.java).번호확인인증(value)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        Log.e(tagName, "$methodName onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            Log.e(tagName, "$methodName onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, res["res"].asString, Toast.LENGTH_SHORT).show()
//                                NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)


                            } else {
                                Toast.makeText(activity, "인증번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                                result = false
                            }
                        }
                        cont.resumeWith(Result.success(Unit))
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e(tagName, "$methodName onFailure: " + t.message)
                    }
                })
        }
        return result //비동기 통신 결과 성공시 r은 true를 반환함
    }




    private suspend fun 새비밀번호요청(): Boolean {
        val methodName = Thread.currentThread().stackTrace[1].methodName //현재 메소드 이름 얻기
        var result = true
        val value = mapOf(
            Pair("user_pwd", binding.findpwPwVerifyInput.text.toString()),
            Pair("user_email", binding.findpwEmailInput.text.toString())
        )

        suspendCoroutine { cont: Continuation<Unit> ->

            //이곳의 서버요청시 인증됐다면 true를 받고, 서버에서는 그 인증번호 레코드를 테이블에서 지워야함.
            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.HttpLogin::class.java).새비밀번호요청(value)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        Log.e(tagName, "$methodName onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            Log.e(tagName, "$methodName onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, res["res"].asString, Toast.LENGTH_SHORT).show()
//                                NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)
                                showToast("새 비밀번호로 변경하였습니다.")
                                //변경 완료하면, 로그인 페이지로 팝하면서 돌아간다.
                                findNavController().navigate(R.id.action_global_loginFm, Bundle.EMPTY,
                                     NavOptions.Builder().setPopUpTo(R.id.action_global_loginFm, false).build()
                                )

                            } else {
                                Toast.makeText(activity, "업데이트 작업이 실패했습니다.", Toast.LENGTH_SHORT).show()
                                result = false
                            }
                        }
                        cont.resumeWith(Result.success(Unit))
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e(tagName, "$methodName onFailure: " + t.message)
                    }
                })
        }
        return result //비동기 통신 결과 성공시 r은 true를 반환함
    }





    /**
     * 비밀번호 찾기 시 사용자 정보 항목 & 형식 유효성 검사를 위한 텍스트 와쳐 생성!
     */
    private fun validationCheck() {
        // 필수 입력란 모두 충족되는지 관찰하는 옵져버 생성
        val verifyArray = arrayOf(false, false)
        val verifyForm = MutableLiveData<Array<Boolean>>()
        verifyForm.observe(viewLifecycleOwner) { booleans ->
            var count = 0
            for (aBoolean in booleans) {
                if (aBoolean) {
                    count++
                    binding.findpwEmailSendBt.isEnabled = count == 2
                }
            }
//            Log.e(tagName, "verifyArray: $verifyArray")
        }


        val nameWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[0] = validateName()
                verifyForm.value = verifyArray
            }
        }
        val emailWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[1] = validateEmail()
                verifyForm.value = verifyArray
            }
        }

        binding.findpwNameInput.addTextChangedListener(nameWatcher)
        binding.findpwEmailInput.addTextChangedListener(emailWatcher)

    }


    /**
     * 비밀번호 찾기 시 - 새 비밀번호 항목 & 형식 유효성 검사를 위한 텍스트 와쳐 생성!
     */
    private fun validationCheckForNewPw() {
        // 필수 입력란 모두 충족되는지 관찰하는 옵져버 생성
        val verifyArray = arrayOf(false, false)
        val verifyForm = MutableLiveData<Array<Boolean>>()
        verifyForm.observe(viewLifecycleOwner) { booleans ->
            var count = 0
            for (aBoolean in booleans) {
                if (aBoolean) {
                    count++
                    binding.findpwCompleteBt.isEnabled = count == 2 // 형식 충족되면 변경 완료 버튼 활성화함.
                }
            }
//            Log.e(tagName, "verifyArray: $verifyArray")
        }


        val pwWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[0] = validatePw()
                verifyForm.value = verifyArray
            }
        }
        val pwVerifyWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[1] = validatePwVerify()
                verifyForm.value = verifyArray
            }
        }

        binding.findpwPwInput.addTextChangedListener(pwWatcher)
        binding.findpwPwVerifyInput.addTextChangedListener(pwVerifyWatcher)

    }



    private fun validateName(): Boolean {
        val value = String.valueOf(binding.findpwNameInput.text)
        return if (value.isEmpty()) {
            binding.findpwNameTextlayout.error = "이름을 입력해주세요."
            false
        } else {
            binding.findpwNameTextlayout.error = null
            true
        }
    }
    private fun validateEmail(): Boolean {
        val value = String.valueOf(binding.findpwEmailInput.text)
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (value.isEmpty()) {
            binding.findpwEmailTextlayout.error = "이메일을 입력해주세요."
            false
        } else if (!value.matches(Regex(emailPattern))) {
            binding.findpwEmailTextlayout.error = "이메일 형식이 맞지 않습니다."
            false
        } else {
            binding.findpwEmailTextlayout.error = null
            true
        }
    }
    private fun validatePw(): Boolean {
        val value = String.valueOf(binding.findpwPwInput.text)
        val pwPattern = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$"
        return if (value.isEmpty()) {
            binding.findpwPwTextlayout.error = "비밀번호를 입력해주세요."
            false
        } else if (!value.matches(Regex(pwPattern))) {
            binding.findpwPwTextlayout.error = "비밀번호 형식이 맞지 않습니다."
            false
        } else {
            binding.findpwPwTextlayout.error = null
            true
        }
    }
    private fun validatePwVerify(): Boolean {
        val value = String.valueOf(binding.findpwPwInput.text)
        val value2 = String.valueOf(binding.findpwPwVerifyInput.text)
        return if (value.isEmpty()) {
            binding.findpwPwVerifyTextlayout.error = "비밀번호 확인을 입력해주세요."
            false
        } else if (value != value2) {
            binding.findpwPwVerifyTextlayout.error = "비밀번호가 다릅니다."
            false
        } else {
            binding.findpwPwVerifyTextlayout.error = null
            true
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
