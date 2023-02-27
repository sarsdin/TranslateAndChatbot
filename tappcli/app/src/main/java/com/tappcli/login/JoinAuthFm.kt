package com.tappcli.login;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.tappcli.R
import com.tappcli.databinding.LoginFmBinding
import com.tappcli.databinding.LoginJoinAuthFmBinding
import gun0912.tedimagepicker.util.ToastUtil.showToast
import java.util.concurrent.TimeUnit


class JoinAuthFm : Fragment() {

    val tagName = "[JoinAuthFm]"
    lateinit var loginVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView
//    lateinit var rva: HomeFmImgRva
//    lateinit var imgVpa: HomeFmImgVpa

    var mbinding: LoginJoinAuthFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    private lateinit var auth: FirebaseAuth
    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // 번호인증 혹은 기타 다른 인증(구글로그인, 이메일로그인 등) 끝난 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                showToast("인증코드가 전송되었습니다. 90초 이내에 입력해주세요 :)")
                Log.e(tagName, "onVerificationCompleted")
                loginVm.phoneAuthNum = credential.smsCode.toString()
//                binding.phoneAuthEtAuthNum.setText(credential.smsCode.toString())
//                binding.phoneAuthEtAuthNum.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    verifyPhoneNumberWithCode(credential)
                }, 1000)
            }

            // 번호인증 실패 상태
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e(tagName, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                showToast("인증실패")
            }

            // 전화번호는 확인 되었으나 인증코드를 입력해야 하는 상태
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(tagName, "onCodeSent:$verificationId")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId // verificationId 와 전화번호인증코드 매칭해서 인증하는데 사용예정
                resendToken = token
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        loginVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = LoginJoinAuthFmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelCallback()

        binding.tv.setOnClickListener {
            findNavController().navigate(R.id.action_joinAuthFm_to_joinFm)
        }

    }

    private fun initViewModelCallback() {
        with(loginVm) {
            // 인증번호 요청 클릭
            binding.joinPhoneVerifyBt.setOnClickListener {
                //첫요청
                if(!loginVm.isResendPhoneAuth){
                    if(binding.joinPhoneNumberEt.text.toString().trim().isEmpty()){
                        showToast("전화번호를 입력해주세요")
                    }else{
                        startPhoneNumberVerification("+82" + binding.joinPhoneNumberEt.text.toString().substring(1))
                        loginVm.tel = loginVm.etPhoneNum.value.toString() // 전화번호 저장
                        loginVm.isResendPhoneAuth = true
                    }

                //재요청인경우
                }else{
                    binding.timerView.setDefault()
                    if(binding.joinPhoneNumberEt.text.toString().trim().isEmpty()){
                        showToast("전화번호를 입력해주세요")
                    }else{
                        resendVerificationCode("+82" + binding.joinPhoneNumberEt.text.toString().substring(1), resendToken)
                        loginVm.tel = loginVm.etPhoneNum.value.toString() // 전화번호 저장
                    }
                }
                //타이머 스타트
                binding.timerView.start(90000) //1분30초
                binding.timerView.visibility = View.VISIBLE
                binding.joinPhoneConfirmTextlayout.isEnabled = true
                binding.timerView.mCertification.observe(viewLifecycleOwner, Observer {
                    if(it){ //인증시간 안
                        binding.joinPhoneConfirmTextlayout.setError(null)
//                        binding.joinPhoneVerifyBt.setEnabled(true)
                    }else{ //인증시간 끝. 인증 시간이 만료되면 만료됐다고 띄움
                        binding.joinPhoneConfirmTextlayout.setError("시간만료 재인증필요")
//                        binding.joinPhoneVerifyBt.setEnabled(false)
                    }
                })

            }

            // 인증완료 버튼 클릭 시
            binding.joinAuthCompleteBt.setOnClickListener {
                // 휴대폰 인증번호로 인증 및 로그인 실행
                // onCodeSent() 에서 받은 vertificationID 와 문자메시지로 전송한 인증코드값으로 Credintial 만든 후 인증 시도
                try {
                    val phoneCredential = PhoneAuthProvider.getCredential(
                        storedVerificationId,
                        binding.joinPhoneAuthcodeEt.text.toString()
                    )
                    verifyPhoneNumberWithCode(phoneCredential)
                } catch (e: Exception) {
                    Log.d(tagName, e.toString())
                }
            }
//            authComplete.observe(viewLifecycleOwner, Observer {
//            })
        }
    }

    // 전화번호 인증코드 요청
    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.joinPhoneVerifyBt.run {
            text = "재전송"
            setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlue))
//            background = ContextCompat.getDrawable(requireActivity(), R.drawable. )
        }
    }

    // 전화번호 인증코드 재요청
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


    // 전화번호 인증 실행 (onCodeSent() 에서 받은 vertificationID 와
    // 문자로 받은 인증코드로 생성한 PhoneAuthCredential 사용)
    private fun verifyPhoneNumberWithCode(phoneAuthCredential: PhoneAuthCredential) {
        //모든것이 다 맞으면(이미 입력되있으면) 넘어감.
        if (loginVm.tel.isNotBlank() && loginVm.phoneAuthNum.isNotBlank()
            && loginVm.phoneAuthNum == binding.joinPhoneAuthcodeEt.text.toString()
            && loginVm.tel == binding.joinPhoneNumberEt.text.toString()
        ) {
            showToast("인증 성공1")
//            loginVm.tel = binding.joinPhoneNumberEt.text.toString()
//            startActivity(Intent(requireActivity(), MainActivity::class.java))
            findNavController().navigate(R.id.action_joinAuthFm_to_joinFm)
            return
        }

        //안맞으면 파이어베이스에서 전달받은 정보를 이용해 인증함.
        Firebase.auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                showToast("인증 성공")
//                loginVm.tel = binding.joinPhoneNumberEt.text.toString()
                binding.joinPhoneAuthcodeEt.isEnabled = true
//                startActivity(Intent(requireActivity(), MainActivity::class.java))
                findNavController().navigate(R.id.action_joinAuthFm_to_joinFm)

            } else {
                //잘못된 인증번호라고 따로 창에 띄우는 것.
//                binding.phoneAuthTvAuthNum.text = getString(R.string.auth_num_wrong_text)
//                binding.phoneAuthTvAuthNum.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorRed))
                showToast("잘못된 인증번호입니다. 다시입력해주세요.")
                binding.joinPhoneConfirmTextlayout.setError("잘못된 인증번호")
                binding.joinPhoneAuthcodeEt.isEnabled = true
            }
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
