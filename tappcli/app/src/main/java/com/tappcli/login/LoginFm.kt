package com.tappcli.login

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.tappcli.MainActivity
import com.tappcli.MyApp
import com.tappcli.R
import com.tappcli.chbot.LoginVm
import com.tappcli.databinding.LoginFmBinding
import com.tappcli.tr.HomeVm
import com.tappcli.util.Http
import gun0912.tedimagepicker.util.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Int

class LoginFm : Fragment() {

    val tagName = "[LoginFm]"
    val host = "10.0.2.2:5001" //안드로이드는 127.0.0.1에 접근 불가. 대신 미리 지정된 alias를 사용해 접근가능.
    lateinit var loginVm: LoginVm
    lateinit var homeVm: HomeVm

    var mbinding: LoginFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
        homeVm = ViewModelProvider(requireActivity()).get(HomeVm::class.java)
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

        //cheat login
        binding.toolbarTv.setOnClickListener {
            치트키()
        }

        //로그인버튼 클릭
        binding.loginLoginBt.setOnClickListener {
            val infoMap = HashMap<kotlin.String, kotlin.String>()
            infoMap["user_email"] = binding.loginMainEmailInput.text.toString()
            infoMap["user_pwd"] = binding.loginMainPwdInput.text.toString()

            Http.getRetrofitInstance(host)!!.create(Http.HttpLogin::class.java).login(infoMap)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        Log.e("[LoginFm]", "로그인 onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            Log.e("[LoginFm]", "로그인 onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, "로그인 하였습니다.", Toast.LENGTH_SHORT).show()
                                ToastUtil.showToast("로그인 하였습니다.")

                                homeVm.liveHistoryL.value = res["histories"].asJsonArray
                                homeVm.liveFavoriteL.value = res["favorites"].asJsonArray

                                //로그인하면 이렇게 앱레벨에서 회원정보를 업데이트해줌.
                                MyApp.id = res["user_no"].asInt
                                MyApp.liveLoginState.value = res["user_no"].asInt
                                MyApp.email = res["user_email"].asString
                                MyApp.nick = res["user_nick"].asString

                                NavHostFragment.findNavController(this@LoginFm).navigate(R.id.action_global_homeFm)
//                                findNavController().navigate(R.id.action_global_homeFm)

                            } else {
                                ToastUtil.showToast("회원정보가 맞지 않습니다.")
//                                Toast.makeText(activity, "회원정보가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e("[LoginFm]", "로그인 onFailure: " + t.message)
                    }
                })



        }

    }




    override fun onResume() {
        super.onResume()
        //로그인 페이지에서는 히스토리 바텀시트 안보이기
        (requireActivity() as MainActivity).binding.includeLayout.root.visibility = View.GONE
    }



    override fun onDestroyView() {
        super.onDestroyView()
        //로그인 페이지에서는 히스토리 바텀시트 안보이기 해제
        (requireActivity() as MainActivity).binding.includeLayout.root.visibility = View.VISIBLE
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



    private fun 치트키() {
        val infoMap = HashMap<kotlin.String, kotlin.String>()
        infoMap["user_email"] = "test@test.com"
        infoMap["user_pwd"] = "tjfwjdahr1!"

        Http.getRetrofitInstance(host)!!.create(Http.HttpLogin::class.java).login(infoMap)!!
            .enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    Log.e("[LoginFm]", "로그인 onResponse: 통신성공, code는: " + response.code())
                    if (response.isSuccessful) {
                        val res = response.body()
                        Log.e("[LoginFm]", "로그인 onResponse: 통신성공, body는: " + res.toString())
                        if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, "로그인 하였습니다.", Toast.LENGTH_SHORT).show()
                            ToastUtil.showToast("로그인 하였습니다.")

                            homeVm.liveHistoryL.value = res["histories"].asJsonArray
                            homeVm.liveFavoriteL.value = res["favorites"].asJsonArray


                            //로그인하면 이렇게 앱레벨에서 회원정보를 업데이트해줌.
                            MyApp.id = res["user_no"].asInt
                            MyApp.liveLoginState.value = res["user_no"].asInt
                            MyApp.email = res["user_email"].asString
                            MyApp.nick = res["user_nick"].asString

                            NavHostFragment.findNavController(this@LoginFm).navigate(R.id.action_global_homeFm)
//                                findNavController().navigate(R.id.action_global_homeFm)

                        } else {
                            ToastUtil.showToast("회원정보가 맞지 않습니다.")
//                                Toast.makeText(activity, "회원정보가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("[LoginFm]", "로그인 onFailure: " + t.message)
                }
            })
    }

}