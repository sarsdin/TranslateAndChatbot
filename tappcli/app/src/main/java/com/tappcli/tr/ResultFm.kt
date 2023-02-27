package com.tappcli.tr

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_ALL
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.tappcli.MyApp
import com.tappcli.R
import com.tappcli.databinding.ResultFmBinding
import com.tappcli.util.ContextMenuLoad
import com.tappcli.util.LanguagePack
import com.tappcli.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ResultFm : Fragment() {
    val tagName = "[ResultFm]"
    lateinit var homeVm: HomeVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView
//    lateinit var rva: HomeFmImgRva
//    lateinit var imgVpa: HomeFmImgVpa

    lateinit var tops: TopSheetBehavior<LinearLayout>

    var mbinding: ResultFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언

    var recordDialog: RecordBottomSheetDialog? = null //음성인식 중을 나타내는 바텀시트 다이얼로그뷰
    var mRecognizer: SpeechRecognizer? = null //speech to text 안드로이드 서비스
    lateinit var gApi : GoogleTranslationApi //구글 번역 클래스


//    //바텀시트뷰안의 리사이클러뷰(색깔목록)
//    var btsR: RecyclerView? = null
//    var btsb: BottomSheetBehavior<*>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeVm = ViewModelProvider(requireActivity()).get(HomeVm::class.java)
        recordDialog = RecordBottomSheetDialog(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mbinding = ResultFmBinding.inflate(inflater, container, false)
        gApi = GoogleTranslationApi(requireActivity(), binding, this@ResultFm)
        Log.e(tagName, "왜 안돼냐!?? ")
//        rv = binding.imgRv
//        rv.setHasFixedSize(true)
//        rv.itemAnimator = null
//        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
//            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
////            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
//        }
//        rv.addItemDecoration(GridSpacingItemDecoration(2, 20, true))
//        rv.adapter = HomeFmImgRva(bibleVm, homeVm, this)
//        rva = rv.adapter as HomeFmImgRva

        return binding.root
    }




    var ocrImgUri : Uri? = null  // ocr 이미지픽커로 선택한 파일의 Uri
//    val ocr = LanguagePack(MyApp.application!!)
    private val ocr by lazy {
        LanguagePack(requireActivity().applicationContext)
    }
    var ocrImgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            //우선 기존의 edittext layout 창을 안보이게함. Gone하면 안됌.. 기능은 수행해야하기 때문.
            binding.ety.visibility = View.GONE
            //그리고 반대로 이미지뷰는 보이게 해야함
            binding.ivl.visibility = View.VISIBLE
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data!!
            ocrImgUri = fileUri
            //이미지뷰에 사진 넣기
            (binding.iv as ShapeableImageView).setImageURI(fileUri)
            //받은 uri를 이용해 비트맵객체 생성
            val bitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(ocrImgUri!!))
            //ocr 판독기능 수행
            val resultString = ocr.printOCRResult(bitmap, homeVm.liveCurrentTranslateMode.value.toString())
            binding.et.setText(resultString)


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "선택 취소", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        appBarConfiguration = AppBarConfiguration.Builder(R.id.home_fm).build()
//        navController = Navigation.findNavController(view)
//        NavigationUI.setupWithNavController(
//            binding.homeToolbar,
//            navController!!,
//            appBarConfiguration!!
//        )



        //뒤로가기 버튼 클릭시 뒤로
        binding.backIb.setOnClickListener {
            findNavController().navigateUp()
        }


        //음성 인식 테스트 버튼
        binding.imageButton.setOnClickListener {
            PermissionHelper.checkRecordPermission(requireActivity()) { // 권한 승인된 상태라면
                //소프트키보드 내리기
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.et.windowToken, 0)
                openVoiceDialog() // 음성인식 다이얼로그 오픈
            }
        }

        //번역 서비스 불러오기
        gApi.getTranslateService()


        //한영 전환 버튼(타이틀바) 클릭시
        binding.changeIv.setOnClickListener {
            if(homeVm.liveCurrentTranslateMode.value == "한영"){
                homeVm.liveCurrentTranslateMode.value = "영한"
            }else{
                homeVm.liveCurrentTranslateMode.value = "한영"
            }
        }
        homeVm.liveCurrentTranslateMode.observe(viewLifecycleOwner, Observer {
            if(it == "한영"){
                binding.toolbarTv.text = "한국어"
                binding.toolbarTv2.text = "영어"
            }else{
                binding.toolbarTv.text = "영어"
                binding.toolbarTv2.text = "한국어"
            }
        })

        //버튼에 따른 차이는 아닌것으로 판명됨. - 첫클릭에 버튼클릭이 안먹히는 증상이 있음.
//        binding.ib2.setOnClickListener {
//            if(homeVm.liveCurrentTranslateMode.value == "한영"){
//                homeVm.liveCurrentTranslateMode.value = "영한"
//            }else{
//                homeVm.liveCurrentTranslateMode.value = "한영"
//            }
//        }

        // TTS 버튼(스피커아이콘) 클릭시 result_tv안의 내용 말하기
        val tts = TTS(requireContext())
        binding.fab.setOnClickListener {
            tts.mainFn(binding.resultTv.text.toString())
        }

        // OCR img 버튼 클릭시
        binding.fabImg.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    ocrImgResult.launch(intent)
                }
        }
        // OCR img x 버튼 클릭시
        binding.fabImgX.setOnClickListener {
            binding.et.setText("")
            binding.et.clearFocus()
            binding.iv.setImageURI(null)
            binding.ivl.visibility = View.GONE
            binding.ety.visibility = View.VISIBLE
        }

        //번역할 텍스트 입력 받기후 디바운스 적용
        val watcher = object : TextWatcher {
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
//                Toast.makeText(requireActivity(),"watch active text: ${searchText}",Toast.LENGTH_SHORT).show()
                if (searchText == searchFor)
                    return

                searchFor = searchText
//                Toast.makeText(requireActivity(),"watch active: ${searchFor}",Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    // do our magic
//                    Toast.makeText(requireActivity(),"watch active",Toast.LENGTH_SHORT).show()
                    var 타겟언어코드 : String = "en"
                    var 원본언어코드 : String = "ko"
                    if(homeVm.liveCurrentTranslateMode.value == "한영"){
                        타겟언어코드 = "en"
                        원본언어코드 = "ko"
                    }else{
                        타겟언어코드 = "ko"
                        원본언어코드 = "en"
                    }

                    if(arguments?.get("signal1").toString() == "${MyApp.히스토리_및_즐겨찾기에서_왔음}"){
                        gApi.translate(타겟언어코드, 원본언어코드, MyApp.히스토리_및_즐겨찾기에서_왔음)
                        arguments?.remove("signal1")
                        arguments?.remove("data")
                    }else{
                        gApi.translate(타겟언어코드, 원본언어코드, 0)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
//            {
//                Toast.makeText(requireActivity(),"watch active after text: ${s.toString()}",Toast.LENGTH_SHORT).show()
//                Log.e(tagName, "왜 안돼냐!?? ${s.toString()}")
//            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        }

        //번역입력창에 텍스트와쳐 적용하기
        binding.et.addTextChangedListener(watcher)



        //음성인식 바텀시트 다이얼로그뷰가 만들어졌으면 해제시 작동할 리스너 등록
//        recordDialog?.let {
//            it.setOnDismissListener(object : BottomSheet.OnDismissListener {
//                override fun onDismiss(bottomSheet: BottomSheet) {
//                    dismissVoiceDialog() //해제시 음성인식 서비스 종료시켜야함.
//                }
//            })
//        }


        //텍스트 셀렉트 시 사용자 커스텀 메뉴를 추가하는 콜백.
        val actionModeCallBack = object : ActionMode.Callback2() {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                // 컨텍스트 액션 모드 설정 및 메뉴 생성 코드 작성
//                mode?.title = "Select Text"
//                mode?.menuInflater?.inflate(R.menu.tv_menu, menu)
                ContextMenuLoad(tagName, binding.resultTv).onInitializeMenu(menu!!, mapOf(Pair("다음사전", "사전"))) //수동으로 불러옴.

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.dict -> {
                        // 사전 기능 구현
                        //선택된 텍스트 값 가져오기
                        val selectedText = binding.resultTv.run {
                            text.substring(selectionStart, selectionEnd)
                        }
                        Log.e(tagName, "selectedText: ${selectedText}")

                        return true
                    }
                    else -> return false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                // 컨텍스트 액션 모드 종료시 처리할 코드 작성
            }

            override fun onGetContentRect(mode: ActionMode?, view: View?, outRect: Rect?) {
                super.onGetContentRect(mode, view, outRect)
                Log.e(tagName, "outRect?.bottom: ${outRect?.bottom}")
//                mode?.invalidateContentRect() // outRect에서 위치 좌표를 받아와 화면에서 벋어나는 좌표가 찍히면 이것을 실행했을때 메뉴를 off할 수 있을듯
            }

        }/*.apply {

        }*/

        //텍스트 셀렉트 시 위에서 만든 사용자 커스텀 메뉴를 추가하는 콜백 등록.
        binding.resultTv.customSelectionActionModeCallback = actionModeCallBack
//        binding.resultTv.setOnLongClickListener {
//            binding.resultTv.startActionMode(actionModeCallBack, ActionMode.TYPE_FLOATING)
//            true
//        }




    }









    // 음성으로 검색하기 다이얼로그 보여주기
    private fun openVoiceDialog() {
        recordDialog?.let {
            if (it.isShown.not()) {
                // 키보드 닫기
//                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
//                    binding.searchBarContainer.searchBarEditText.windowToken, 0)

                // 키보드 닫는 시간을 위해 0.5초 뒤에 다이얼로그 보여주기
                CoroutineScope(Dispatchers.Main).launch {
//                   Toast.makeText(requireActivity(),"onon3",Toast.LENGTH_SHORT).show()
                    delay(500L)
                    it.show()
                    if (mRecognizer == null) {
                        mRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity()) // 새로운 SpeechRecognizer를 만드는 팩토리 메서드
                        mRecognizer?.setRecognitionListener(getRecognitionListener())
                    }

//                   Toast.makeText(requireActivity(),"onon4",Toast.LENGTH_SHORT).show()
                    mRecognizer?.startListening(
                        // 여분의 키
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().packageName)
                            .apply {
                                if(homeVm.liveCurrentTranslateMode.value == "한영"){
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 한국어 설정 or en-US
                                }else{
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // 한국어 설정 or en-US
                                }
                            }
                    )
//                 Toast.makeText(requireActivity(),"onon5",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dismissVoiceDialog() {
        mRecognizer?.stopListening() // 음성인식 종료
        mRecognizer?.destroy()
        if (recordDialog?.isShown == true) {
            recordDialog?.dismiss()
        }
    }

    // 음성인식 리스너
    private fun getRecognitionListener(): RecognitionListener {
//        Toast.makeText(requireActivity(),"onon7",Toast.LENGTH_SHORT).show()
        return object : RecognitionListener {

            // 말하기 시작할 준비가되면 호출
            override fun onReadyForSpeech(params: Bundle?) {}

            // 말하기 시작했을 때 호출
            override fun onBeginningOfSpeech() {
//                Toast.makeText(requireActivity(),"말하기가 시작했을때",Toast.LENGTH_SHORT).show()
            }

            // 입력받는 소리의 크기를 알려줌
            override fun onRmsChanged(dB: Float) {
//                Toast.makeText(requireActivity(),"소리크기:${dB}",Toast.LENGTH_SHORT).show()
            }

            // 말을 시작하고 인식이 된 단어를 buffer에 담음
            override fun onBufferReceived(p0: ByteArray?) {}

            // 말하기가 끝났을 때
            override fun onEndOfSpeech() {
//                Toast.makeText(requireActivity(),"말하기가 끝났을때",Toast.LENGTH_SHORT).show()
            }

            // 에러 발생
            override fun onError(error: Int) {
                mRecognizer?.cancel()
                mRecognizer?.startListening(
                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,requireActivity().packageName)
                        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                )
            }

            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            override fun onResults(results: Bundle?) {
                val match = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
//                viewModel.setSearchQuery(match)
//                Toast.makeText(requireActivity(),"onon6",Toast.LENGTH_SHORT).show()
//                binding.resultTv.text = match
                binding.et.setText(match)
                recordDialog?.dismiss()
//                binding.searchProgressBar.visibility = View.VISIBLE // 프로그레스바 보여주기
                // No Result 화면을 가리고, 리사이클러뷰를 띄운다.
//                binding.noResultContainer.visibility = View.GONE
//                binding.searchRecyclerView.visibility = View.VISIBLE
            }

            // 부분 인식 결과를 사용할 수 있을 때 호출
            override fun onPartialResults(p0: Bundle?) {}

            // 향후 이벤트를 추가하기 위해 예약
            override fun onEvent(p0: Int, p1: Bundle?) {}
        }
    }




    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PERMISSION_RECORD_REQUEST_CODE && grantResults.size == PermissionHelper.REQUIRED_PERMISSION_RECORD.size) {
            var isGranted = true

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                }
            }

            if (isGranted) { // 사용자가 권한을 허용했다면
                openVoiceDialog() // 음성 지원 다이얼로그 보여주기
            } else {
                // 사용자가 직접 권한을 거부했다면
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),PermissionHelper.REQUIRED_PERMISSION_LOCATION[0])) {
                    Toast.makeText(requireActivity(),"음성 녹음 권한 필요함",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }



    override fun onResume() {
        super.onResume()
        //위젯 컨트롤
        if(arguments?.get("widget_signal2") == "to_result_fm"){
            Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                binding.et.requestFocus()
                val imm = (requireActivity()).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(binding.et, 0) //직접 뷰를 설정해서 보이는 방법
                imm.showSoftInput(requireActivity().currentFocus, 0) //현재 포커싱된 뷰를 여는 방법
            },100)
            arguments?.clear()
            Log.e(tagName, "widget_signal in onresume")


        } else if(arguments?.get("signal1") == MyApp.히스토리_및_즐겨찾기에서_왔음.toString()){
            //히스토리 & 즐겨찾기 클릭시
            히스토리즐찾클릭()


        }



    }


    fun 히스토리즐찾클릭(){
        arguments?.get("data").toString().run {
            JsonParser.parseString(this).asJsonObject.run {
                Log.e(tagName, Gson().toJson(this))
                if(this.get("favorite_content") == null){
                    binding.et.text = Editable.Factory.getInstance().newEditable(this["history_content"].asString)
                    //그리고, history_content_lang 의 값이 en 이라면 googletranslationapi의 타겟언어코드도 en 으로 설정. ko 라면 ko로..
                    //homeVm에서 mode를 "한영" 으로 바꾸면됨
                }else{
                    binding.et.setText(this["favorite_content"].asString)

                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.et.setText("") //페이지에서 나갈때(이동시)는 항상 검색창 초기화 해줌. 나중에 뒤로가기등으로 돌아오면 자동으로 검색하기때문..
        binding.et.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }


}