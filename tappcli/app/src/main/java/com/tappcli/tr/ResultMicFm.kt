//package com.tappcli.tr
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.speech.RecognitionListener
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.inputmethod.InputMethodManager
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.NavController
//import androidx.navigation.Navigation
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.NavigationUI
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.arthurivanets.bottomsheets.BottomSheet
//import com.tappcli.R
//import com.tappcli.databinding.ResultFmBinding
//import com.tappcli.util.PermissionHelper
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//class ResultMicFm : Fragment() {
//    val tagName = "[ResultMicFm]"
//    lateinit var homeVm: HomeVm
//    var appBarConfiguration: AppBarConfiguration? = null
//    private var navController: NavController? = null
//
//    lateinit var rv: RecyclerView
////    lateinit var rva: HomeFmImgRva
////    lateinit var imgVpa: HomeFmImgVpa
//
//    lateinit var tops: TopSheetBehavior<LinearLayout>
//
//    var mbinding: ResultFmBinding? = null
//    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언
//
//    var recordDialog: RecordBottomSheetDialog? = null //음성인식 중을 나타내는 바텀시트 다이얼로그뷰
//    var mRecognizer: SpeechRecognizer? = null //speech to text 안드로이드 서비스
//    lateinit var gApi : GoogleTranslationApi //구글 번역 클래스
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        homeVm = ViewModelProvider(requireActivity()).get(HomeVm::class.java)
//        recordDialog = RecordBottomSheetDialog(requireActivity())
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        mbinding = ResultFmBinding.inflate(inflater, container, false)
//        gApi = GoogleTranslationApi(requireActivity(),binding, this)
//
////        rv = binding.imgRv
////        rv.setHasFixedSize(true)
////        rv.itemAnimator = null
////        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
////            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
//////            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
////        }
////        rv.addItemDecoration(GridSpacingItemDecoration(2, 20, true))
////        rv.adapter = HomeFmImgRva(bibleVm, homeVm, this)
////        rva = rv.adapter as HomeFmImgRva
//
//        return binding.root
//    }
//
//    //editImageActivity 를 열었던 결과를 콜백받음.
//    val editImageActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
//        val resultCode = result.resultCode
//        val data = result.data
//        if(resultCode == Activity.RESULT_OK){
////            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(UnsplashPickerActivity.EXTRA_PHOTOS)
////            if (photos != null) {
////                homeVm.unsplashL = photos
////                homeVm.liveUnsplashL.value = homeVm.unsplashL
////            }
//            if (data != null) {
//                Log.e(tagName, "editImageActivityForResult: ${data.getStringExtra("editFinished")}")
//            } else {
//                Log.e(tagName, "editImageActivityForResult: data is null")
//            }
//        }
//    }
//
//    //unsplash picker Activity 를 열었던 결과를 콜백받음.
////    val unsplashForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
////        val resultCode = result.resultCode
////        val data = result.data
////
////        if(resultCode == Activity.RESULT_OK){
////            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(
////                UnsplashPickerActivity.EXTRA_PHOTOS)
////            if (photos != null) {
////                homeVm.unsplashL = photos
////                homeVm.liveUnsplashL.value = homeVm.unsplashL
////            }
////        }
////    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        appBarConfiguration = AppBarConfiguration.Builder(R.id.home_fm).build()
////        navController = Navigation.findNavController(view)
////        NavigationUI.setupWithNavController(
////            binding.homeToolbar,
////            navController!!,
////            appBarConfiguration!!
////        )
//
//        //뒤로가기 버튼 클릭시 뒤로
//        binding.backIb.setOnClickListener {
//            findNavController().navigateUp()
//        }
//
//
//        //음성 인식 테스트 버튼
//        binding.imageButton.setOnClickListener {
//            PermissionHelper.checkRecordPermission(requireActivity()) { // 권한 승인된 상태라면
//                openVoiceDialog() // 음성인식 다이얼로그 오픈
//            }
//        }
//
//        //번역 서비스 불러오기
//        gApi.getTranslateService()
//        //번역 버튼 클릭시 번역하기
//        binding.translateIb.setOnClickListener {
//            var 타겟언어코드 : String = "en"
//            var 원본언어코드 : String = "ko"
//            if(homeVm.liveCurrentTranslateMode.value == "한영"){
//                타겟언어코드 = "en"
//                원본언어코드 = "ko"
//            }else{
//                타겟언어코드 = "ko"
//                원본언어코드 = "en"
//            }
//            gApi.translate(타겟언어코드, 원본언어코드)
//        }
//
//        //음성인식 바텀시트 다이얼로그뷰가 만들어졌으면 해제시 작동할 리스너 등록
////        recordDialog?.let {
////            it.setOnDismissListener(object : BottomSheet.OnDismissListener {
////                override fun onDismiss(bottomSheet: BottomSheet) {
////                    dismissVoiceDialog() //해제시 음성인식 서비스 종료시켜야함.
////                }
////            })
////        }
//
//    }
//
//
//    // 음성으로 검색하기 다이얼로그 보여주기
//    private fun openVoiceDialog() {
//        recordDialog?.let {
//            if (it.isShown.not()) {
//                // 키보드 닫기
////                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
////                    binding.searchBarContainer.searchBarEditText.windowToken, 0)
//
//                // 키보드 닫는 시간을 위해 0.5초 뒤에 다이얼로그 보여주기
//                CoroutineScope(Dispatchers.Main).launch {
////                   Toast.makeText(requireActivity(),"onon3",Toast.LENGTH_SHORT).show()
//                    delay(500L)
//                    it.show()
//                    if (mRecognizer == null) {
//                        mRecognizer =
//                            SpeechRecognizer.createSpeechRecognizer(requireActivity()) // 새로운 SpeechRecognizer를 만드는 팩토리 메서드
//                        mRecognizer?.setRecognitionListener(getRecognitionListener())
//                    }
//
////                   Toast.makeText(requireActivity(),"onon4",Toast.LENGTH_SHORT).show()
//                    mRecognizer?.startListening(
//                        // 여분의 키
//                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().packageName)
//                            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 한국어 설정
//                    )
////                 Toast.makeText(requireActivity(),"onon5",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun dismissVoiceDialog() {
//        mRecognizer?.stopListening() // 음성인식 종료
//        mRecognizer?.destroy()
//        if (recordDialog?.isShown == true) {
//            recordDialog?.dismiss()
//        }
//    }
//
//    // 음성인식 리스너
//    private fun getRecognitionListener(): RecognitionListener {
////        Toast.makeText(requireActivity(),"onon7",Toast.LENGTH_SHORT).show()
//        return object : RecognitionListener {
//
//            // 말하기 시작할 준비가되면 호출
//            override fun onReadyForSpeech(params: Bundle?) {}
//
//            // 말하기 시작했을 때 호출
//            override fun onBeginningOfSpeech() {
////                Toast.makeText(requireActivity(),"말하기가 시작했을때",Toast.LENGTH_SHORT).show()
//            }
//
//            // 입력받는 소리의 크기를 알려줌
//            override fun onRmsChanged(dB: Float) {
////                Toast.makeText(requireActivity(),"소리크기:${dB}",Toast.LENGTH_SHORT).show()
//            }
//
//            // 말을 시작하고 인식이 된 단어를 buffer에 담음
//            override fun onBufferReceived(p0: ByteArray?) {}
//
//            // 말하기가 끝났을 때
//            override fun onEndOfSpeech() {
////                Toast.makeText(requireActivity(),"말하기가 끝났을때",Toast.LENGTH_SHORT).show()
//            }
//
//            // 에러 발생
//            override fun onError(error: Int) {
//                mRecognizer?.cancel()
//                mRecognizer?.startListening(
//                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                        .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,requireActivity().packageName)
//                        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
//                )
//            }
//
//            // 인식 결과가 준비되면 호출
//            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
//            override fun onResults(results: Bundle?) {
//                var match = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
////                viewModel.setSearchQuery(match)
////                Toast.makeText(requireActivity(),"onon6",Toast.LENGTH_SHORT).show()
////                binding.resultTv.text = match
//                binding.et.setText(match)
//                recordDialog?.dismiss()
////                binding.searchProgressBar.visibility = View.VISIBLE // 프로그레스바 보여주기
//                // No Result 화면을 가리고, 리사이클러뷰를 띄운다.
////                binding.noResultContainer.visibility = View.GONE
////                binding.searchRecyclerView.visibility = View.VISIBLE
//            }
//
//            // 부분 인식 결과를 사용할 수 있을 때 호출
//            override fun onPartialResults(p0: Bundle?) {}
//
//            // 향후 이벤트를 추가하기 위해 예약
//            override fun onEvent(p0: Int, p1: Bundle?) {}
//        }
//    }
//
//
//
//
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == PermissionHelper.PERMISSION_RECORD_REQUEST_CODE && grantResults.size == PermissionHelper.REQUIRED_PERMISSION_RECORD.size) {
//            var isGranted = true
//
//            // 모든 퍼미션을 허용했는지 체크합니다.
//            for (result in grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    isGranted = false
//                    break
//                }
//            }
//
//            if (isGranted) { // 사용자가 권한을 허용했다면
//                openVoiceDialog() // 음성 지원 다이얼로그 보여주기
//            } else {
//                // 사용자가 직접 권한을 거부했다면
//                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),PermissionHelper.REQUIRED_PERMISSION_LOCATION[0])) {
//                    Toast.makeText(requireActivity(),"음성 녹음 권한 필요함",Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//    }
//
//
//
//    override fun onResume() {
//        super.onResume()
//
//
//    }
//
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        mbinding = null
//    }
//
//
//}