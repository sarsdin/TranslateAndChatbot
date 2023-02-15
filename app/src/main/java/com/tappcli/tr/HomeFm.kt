package com.tappcli.tr

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tappcli.MyApp
import com.tappcli.R
import com.tappcli.databinding.HomeFmBinding
import com.tappcli.util.AlertDialogCustom
import com.tappcli.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFm : Fragment() {
    val tagName = "[HomeFm]"
    lateinit var homeVm: HomeVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null
    private var backKeyPressedTime: Long = 0

    lateinit var rv: RecyclerView
//    lateinit var rva: HomeFmImgRva
//    lateinit var imgVpa: HomeFmImgVpa

    lateinit var tops: TopSheetBehavior<LinearLayout>

    var mbinding: HomeFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언

    var recordDialog: RecordBottomSheetDialog? = null
    var mRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        UnsplashPhotoPicker.init(
//            MyApp.application, // application
//            BuildConfig.API_KEY,
//            BuildConfig.API_SKEY
//            /* optional page size */
//        )

        homeVm = ViewModelProvider(requireActivity()).get(HomeVm::class.java)
        recordDialog = RecordBottomSheetDialog(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mbinding = HomeFmBinding.inflate(inflater, container, false)

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


        //절화면 탑시트 어댑터 세팅
//        val lm = LinearLayoutManager(binding.root.context)
//        lm.orientation = LinearLayoutManager.HORIZONTAL
//        btsR = binding.includeLayout.
//        btsR.setLayoutManager(lm)
//        btsR.setAdapter(BibleVerseBtsRva(bibleVm, this))

//        //탑시트뷰 적용부분
//        tops = TopSheetBehavior.from(binding.includeLayout.root)
//        tops.state = TopSheetBehavior.STATE_HIDDEN
//        tops.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback(){
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                TODO("Not yet implemented")
//            }
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                TODO("Not yet implemented")
//            }
//        })



        return binding.root
    }

    //editImageActivity 를 열었던 결과를 콜백받음.
    val editImageActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if(resultCode == Activity.RESULT_OK){
//            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(UnsplashPickerActivity.EXTRA_PHOTOS)
//            if (photos != null) {
//                homeVm.unsplashL = photos
//                homeVm.liveUnsplashL.value = homeVm.unsplashL
//            }
            if (data != null) {
                Log.e(tagName, "editImageActivityForResult: ${data.getStringExtra("editFinished")}")
            } else {
                Log.e(tagName, "editImageActivityForResult: data is null")
            }
        }
    }

    //unsplash picker Activity 를 열었던 결과를 콜백받음.
//    val unsplashForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
//        val resultCode = result.resultCode
//        val data = result.data
//
//        if(resultCode == Activity.RESULT_OK){
//            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(
//                UnsplashPickerActivity.EXTRA_PHOTOS)
//            if (photos != null) {
//                homeVm.unsplashL = photos
//                homeVm.liveUnsplashL.value = homeVm.unsplashL
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleOnBackPressed() //뒤로가기 종료 구현부
//        appBarConfiguration = AppBarConfiguration.Builder(R.id.home_fm).build()
//        navController = Navigation.findNavController(view)
//        NavigationUI.setupWithNavController(
//            binding.homeToolbar,
//            navController!!,
//            appBarConfiguration!!
//        )



        //로그인 유무 확인후 그 페이지로 이동할지 물음.
        binding.homeToolbarIv.setOnClickListener {
            if(MyApp.id.isEmpty()){
                lifecycleScope.launch {
                    val res = AlertDialogCustom(requireContext(),"로그인", "로그인 화면으로 이동하시겠습니까?").show()
                    Log.d("디버그태그","res: $res")
                    if(res){
                        findNavController().navigate(R.id.action_global_loginFm)
                    }
                }
            }
        }


        //change_iv 한영 전환 버튼
        binding.changeIv.setOnClickListener{
            if (homeVm.liveCurrentTranslateMode.value == "한영") {
//                homeVm.currentTranslateMode = "영한"
                homeVm.liveCurrentTranslateMode.value = "영한"
            } else {
//                homeVm.currentTranslateMode = "한영"
                homeVm.liveCurrentTranslateMode.value = "한영"
            }
        }
        homeVm.liveCurrentTranslateMode.observe(viewLifecycleOwner, Observer {
            if(it == "한영"){
                binding.bt1.text = "한국어"
                binding.bt2.text = "영어"
            }else{
                binding.bt1.text = "영어"
                binding.bt2.text = "한국어"
            }
        })




        //번역 텍스트뷰를 클릭하면 resultFm 페이지로 넘김.
        binding.textView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFm_to_resultFm)
        }

        //음성인식 버튼 클릭하면 resultMicFm 페이지로 넘김
        binding.micIb.setOnClickListener {
            findNavController().navigate(R.id.action_homeFm_to_resultFm)
//            findNavController().navigate(R.id.action_homeFm_to_resultMicFm)
        }

        Log.e(tagName, "왜 안돼냐!?? ")


    }



    private fun dismissVoiceDialog() {
        mRecognizer?.stopListening() // 음성인식 종료
        mRecognizer?.destroy()
        if (recordDialog?.isShown == true) {
            recordDialog?.dismiss()
        }
    }







    override fun onResume() {
        super.onResume()
        //        MainActivity mainA = (MainActivity)requireActivity();
//        ((MainActivity)requireActivity()).binding.mainToolbar.getMenu().findItem(R.id.main_toolbar_menu_logout).setVisible(false);
//        ((MainActivity)requireActivity()).binding.mainToolbar.getMenu().findItem(R.id.app_bar_search).setEnabled(false);
//        ((SearchView) mainA.binding.mainToolbar.getMenu().findItem(R.id.app_bar_search).getActionView()).setVisibility(View.GONE);
//        Log.e("test", "test: "+((MainActivity)requireActivity()).binding.mainToolbar.getMenu().findItem(R.id.app_bar_search).setVisible(false));

        //상단바 프로필 이미지 클릭시
//        binding.homeToolbarIv.setOnClickListener {
//            findNavController().navigate(R.id.action_global_myProfileFm)
//        }
        //상단바 프로필 이미지 로딩
//        ImageHelper.getImageUsingGlide(requireActivity(), MyApp.userInfo.user_image, binding.homeToolbarIv)


        //vm안의 값에 따라 뷰의 텍스트 자동변경
//        if(homeVm.currentTranslateMode == "한영"){
//            binding.bt1.text = "한국어"
//            binding.bt2.text = "영어"
//        } else {
//            binding.bt1.text = "영어"
//            binding.bt2.text = "한국어"
//        }


        // widget_signal 체크부분
        Log.e(tagName, "widget_signal in onresume: ${requireActivity().intent.extras?.get("widget_signal")}")
        if(requireActivity().intent.extras?.get("widget_signal") == "translate") {
            Log.e(tagName, "widget_signal in onresume")
            requireActivity().intent.removeExtra("widget_signal") //remove로 하기..
//            requireActivity().intent.extras?.getBundle("widget_signal")?.clear()
            findNavController().navigate(R.id.action_homeFm_to_resultFm, Bundle().apply { putString("widget_signal2", "to_result_fm") })
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }

    private fun handleOnBackPressed() { //뒤로가기 종료 구현부
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { //백버튼을 조각에서 조종하기 위한 메소드.
//                        Toast.makeText(requireActivity(),"test1111",Toast.LENGTH_SHORT).show();
//                            requireActivity().finish(); //AlertDialog 창으로 종료할지 물어야함
                    if (parentFragmentManager.backStackEntryCount == 0) {
//                        if(getParentFragmentManager().getBackStackEntryAt(0).getId() == R.id.startFm){
                        뒤로가기종료() //두번 클릭시 종료처리
                    } else {
                        val navcon = NavHostFragment.findNavController(this@HomeFm)
                        navcon.navigateUp() //뒤로가기(백스택에서 뒤로가기?)
                    }
                }
            })
    }

    private fun 뒤로가기종료() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(requireActivity(),"뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            dismissVoiceDialog() //음성인식 다이얼로그 닫기
            requireActivity().finish()
            //            super.onBackPressed();
        }
    }
}