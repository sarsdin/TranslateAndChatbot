package com.tappcli.login;

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.*
import com.google.gson.JsonObject
import com.tappcli.MyApp
import com.tappcli.databinding.LoginFindIdFmBinding
import com.tappcli.util.Http
import gun0912.tedimagepicker.util.ToastUtil.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Int
import kotlin.Pair
import kotlin.Result
import kotlin.Throwable
import kotlin.Unit
import kotlin.apply
import kotlin.arrayOf
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.toString


class LoginFindIdFm : Fragment() {

    val tagName = "[${this.javaClass.canonicalName}]"
    lateinit var loginVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView

    var mbinding: LoginFindIdFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mbinding = LoginFindIdFmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationCheck() //입력항목 유효성 검사

        //아이디 찾기 클릭시
        binding.findBt.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = 아이디찾기()
            }
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        }

        //tv2 클릭시 복사하기
        binding.resultInfoTv2.setOnClickListener {
            showToast("아이디를 복사하였습니다.")
        }


        // up button 클릭시
        binding.toolbarSlide.setOnClickListener {
            //로그인 페이지까지 팝하면서 날라감.
//            findNavController().navigate(R.id.action_global_loginFm, Bundle.EMPTY,
//                NavOptions.Builder().setPopUpTo(R.id.action_global_loginFm, false).build()
//            )
            findNavController().navigateUp()
        }

        state = MutableLiveData<Boolean>().apply {
            observe(viewLifecycleOwner){
                if(it){
                    binding.resultInfoTv.visibility = View.GONE
                    binding.resultInfoTv2.visibility = View.VISIBLE
                }else{
                    binding.resultInfoTv.visibility = View.VISIBLE
                    binding.resultInfoTv2.visibility = View.GONE
                }
            }
        }


    }


    lateinit var state : MutableLiveData<Boolean> //통신결과에 따라 ui 조정
    private suspend fun 아이디찾기(): Boolean {
        var result = true
        val value = mapOf(
            Pair("user_phone", binding.input.text.toString()),
            Pair("user_name", binding.nameInput.text.toString())
        )

        suspendCoroutine { cont: Continuation<Unit> ->

            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.HttpLogin::class.java).findId(value)!!
                .enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        Log.e(tagName, "isEmailRedundant onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            Log.e(tagName, "isEmailRedundant onResponse: 통신성공, body는: " + res.toString())
                            if (res!!["res"].asBoolean) {
//                                Toast.makeText(activity, res["res"].asString, Toast.LENGTH_SHORT).show()
//                                NavHostFragment.findNavController(this@JoinFm).navigate(R.id.action_global_homeFm)
                                binding.textlayout.error = null
                                state.value = true
                                val reText = String.format(
                                    "등록된 아이디는 <span style='color:%s'>%s</span> 입니다.",
                                    "#009688",
                                    "'${res["user_email"].asString}'"
                                )
                                binding.resultInfoTv2.setText(Html.fromHtml(reText, FROM_HTML_MODE_COMPACT))
                                MyApp.clipBoard(requireActivity(),"any",null, res["user_email"].asString )


                            } else {
                                Toast.makeText(activity, "아이디 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                binding.textlayout.error = null
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


    /**
     * 아이디 찾기 시 항목 & 형식 유효성 검사를 위한 텍스트 와쳐 생성!
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
                    binding.findBt.isEnabled = count == 2
                }
            }
//            Log.e(tagName, "verifyArray: $verifyArray")
        }


        val phoneNumberWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                verifyArray[0] = validatePhoneNumber()
                verifyForm.value = verifyArray
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

        binding.input.addTextChangedListener(phoneNumberWatcher)
        binding.nameInput.addTextChangedListener(nameWatcher)

    }




    override fun onResume() {
        super.onResume()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }

    private fun validatePhoneNumber(): Boolean {
        val value = String.valueOf(binding.input.text)
        return if (value.isEmpty()) {
            binding.textlayout.error = "휴대폰 번호를 입력해주세요."
            false
        } else {
            binding.textlayout.error = null
            true
        }
    }
    private fun validateName(): Boolean {
        val value = String.valueOf(binding.nameInput.text)
        return if (value.isEmpty()) {
            binding.nameTextlayout.error = "이름을 입력해주세요."
            false
        } else {
            binding.nameTextlayout.error = null
            true
        }
    }

}