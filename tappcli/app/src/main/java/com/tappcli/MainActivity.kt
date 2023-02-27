package com.tappcli
import android.util.Log

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import com.tappcli.databinding.MainActivityBinding
import com.tappcli.tr.FavoriteFm
import com.tappcli.tr.HomeVm
import com.tappcli.tr.LockScreenService
import com.tappcli.tr.HistoryBtsRva
import com.tappcli.util.AlertDialogCustom
import com.tappcli.util.Http
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val tagName = "[MainActivity]"
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: MainActivityBinding
    lateinit var homeVm: HomeVm
    lateinit var navHostFragment : NavHostFragment

    //바텀시트뷰안의 리사이클러뷰(색깔목록)
    var btsR: RecyclerView? = null
    var btsb: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().setNavigationBarColor(Color.BLACK) //하단 소프트바 색상 변경. 투명:TRASPARENT

        homeVm = ViewModelProvider(this).get(HomeVm::class.java)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_cloud_24)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        //fragmentcontainerview 사용시 supportFMmanager를 이용하여 컨트롤러를 등록해야 illegalerror가 안뜸
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main_activity) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.root) //최상위 Destination 설정
        binding.navView.setupWithNavController(navController) //드로우뷰와 네비게이션 컨트롤러을 연동하여 상태저장 및 복원 관리

        //이건 작동안함. toolbar가 gone 상태임. - 차후 homefm의 toolbar에 있는 서랍아이콘에 open()을 연동하는게 좋음.
//        binding.toolbar.setNavigationOnClickListener {
//            binding.root.open()
//        }

        binding.navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
//                item.setChecked(false)
                binding.root.close() //드로우뷰 닫기
//                val id = item.itemId
//                val title = item.title.toString()

                when(item.itemId) {
                    R.id.chatBotFm -> {
//                        Toast.makeText(this@MainActivity,"chatBotFm 실행",Toast.LENGTH_SHORT).show()
//                        binding.root.closeDrawer(GravityCompat.START)
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_main_activity)
                            .navigate(R.id.action_global_chatBotFm)
                    }
                    R.id.loginFm -> {
                        if( MyApp.liveLoginState.value == 0){
                            moveLoginPageDialog()

                        }else{
                            //로그아웃해야함
                            MyApp.logOut()
                            homeVm.historyL.removeAll {
                                true
                            }
                        }
                    }
                    R.id.historyBts -> {
                        if( MyApp.liveLoginState.value == 0){
                            Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                                item.isChecked  = false
                            },100)
                            moveLoginPageDialog()
                        } else {
                            //히스토리 바텀시트뷰 오픈하기
                            btsb?.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    R.id.favorite -> {
                        if( MyApp.liveLoginState.value == 0){
                            Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                                item.isChecked  = false
                            },100)
                            moveLoginPageDialog()
                        } else {
                            //즐겨찾기 페이지로 가기
                            Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_main_activity)
                                .navigate(R.id.action_global_favoriteFm)
                        }
                    }
                }

                return true
            }
        })




        //오버레이 권한 획득 체크
        checkPermission()
        lockScreen()

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
//        }

        //히스토리 바텀시트 셋팅
        bts()


        //히스토리 값이 변화하면 리사이클러뷰 갱신해주기.
        homeVm.liveHistoryL.observe(this, Observer {
            btsR?.adapter?.notifyDataSetChanged()
        })

        //즐겨찾기 값이 변화하면 리사이클러뷰 갱신해주기.
        homeVm.liveFavoriteL.observe(this, Observer {
//            btsR?.adapter?.notifyDataSetChanged()
//            supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment

            //현재 활성된 프래그먼트 찾는 법.
            val currentFragment = NavHostFragment.findNavController(navHostFragment).currentDestination?.let {
                Log.e(tagName, "현재 프래그먼트 currentDestination: ${it}")
                if(it.id == R.id.favoriteFm){
                    Log.e(tagName, "현재 프래그먼트 == R.id.favoriteFm: ${it}, ${it.id}")
    //                supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.findFragmentById(it.id) //id로 찾는 방법
                    ((supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment) as FavoriteFm)
                        .rv.adapter?.notifyDataSetChanged()
                }

            }

            // ClassName 으로 현재 fragment 찾는 방법
//            private fun getCurrentFragment(): Fragment? {
//                val currentNavHost = supportFragmentManager.findFragmentById(R.id.nav_host_id)
//                val currentFragmentClassName = ((this as NavHost).navController.currentDestination as FragmentNavigator.Destination).className
//                return currentNavHost?.childFragmentManager?.fragments?.filterNotNull()?.find {
//                    it.javaClass.name == currentFragmentClassName
//                }
//            }
        })


    }


    /**
     * 로그인 페이지로 이동할지 다이얼로그 띄움
     * */
    fun moveLoginPageDialog() {
        lifecycleScope.launch {
            val res = AlertDialogCustom(this@MainActivity,"로그인", "로그인 화면으로 이동하시겠습니까?").show()
            Log.d("디버그태그","res: $res")
            if(res){
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_main_activity)
                    .navigate(R.id.action_global_loginFm)
            }
        }
    }


    fun bts(){
        //절화면 바텀시트 어댑터 세팅
        val lm = LinearLayoutManager(binding.root.context)
        lm.orientation = LinearLayoutManager.VERTICAL
        btsR = binding.includeLayout.btsList
        btsR!!.setLayoutManager(lm)
        btsR!!.setAdapter(HistoryBtsRva(homeVm, this))

//        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL } //코틀린 문법
        btsb = BottomSheetBehavior.from(binding.includeLayout.getRoot())
//        btsb = BottomSheetBehavior.from(binding.getRoot().findViewById(R.id.include_layout));
//        btsb = BottomSheetBehavior.from(binding.getRoot().findViewById(R.id.bible_verse_bts));
        //        btsb = BottomSheetBehavior.from(binding.getRoot().findViewById(R.id.include_layout));
//        btsb = BottomSheetBehavior.from(binding.getRoot().findViewById(R.id.bible_verse_bts));

//        binding.includeLayout.setVm(bibleVm) //vm databinding 변수에 vm 맵핑


        //바텀시트뷰 감추기
        (btsb as BottomSheetBehavior<*>).setState(BottomSheetBehavior.STATE_HIDDEN) //일단 시작을 감추기로 시작함

        (btsb as BottomSheetBehavior<*>).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.navView.checkedItem?.let { it.isChecked = true }
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.navView.checkedItem?.let { it.isChecked = false }
                    }
                    else -> {}
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


    }

    fun lockScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            //setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }

    override fun onResume() {
        super.onResume()
        MyApp.liveLoginState.observe(this, Observer {
            val loginItem = (binding.navView.menu.findItem(R.id.loginFm)) /*as NavigationMenuItemView*/
//            val loginItem = (menu.findItem(R.id.loginFm).actionView) as MenuItem
            Log.e(tagName, "onCreateOptionMenu 옵져버 작동하냐?")
            if(it != 0){ //0이아니라면 로그인 상태로 간주하고 드로뷰에서 로그인 버튼 없앰
                loginItem.title = "로그아웃"

            }else{
                loginItem.title = "로그인"
            }
        })


        //검색 이력 전체삭제
        binding.includeLayout.bt.setOnClickListener {
            기록삭제물어보기()
        }

    }


    /**
     * 기록 삭제할지 다이얼로그 띄움
     * */
    fun 기록삭제물어보기() {
        lifecycleScope.launch {
            val res = AlertDialogCustom(this@MainActivity,"기록 삭제", "전체 기록을 삭제하시겠습니까?").show()
            Log.d("디버그태그","res: $res")
            if(res){
                히스토리전체삭제()
            }
        }
    }


    private fun 히스토리전체삭제() {
        Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.Translate::class.java)
            .히스토리전체삭제(MyApp.id.toString())!!
            .enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {

                    Log.e(tagName, "히스토리전체삭제 onResponse: 통신성공, code는: " + response.code())
                    if (response.isSuccessful) {
                        val res = response.body()
                        if (res!!["res"].asBoolean) {
                            homeVm.liveHistoryL.value = res["histories"].asJsonArray

                        } else {
//                            ToastUtil.showToast("회원정보가 맞지 않습니다.")
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e(tagName, "히스토리전체삭제 onFailure: " + t.message)
                }
            })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            android.R.id.home -> {
//                binding.root.openDrawer(GravityCompat.START)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_main_activity)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }



    fun checkPermission() {
        // 마쉬멜로 버전 이상이면 다른 앱위에 표시가능한지 알아보고,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //불가능이면 현재 앱의 패키지명을 이용해 권한을 요청하고 onActivityResult()로 결과를 받는다.
            if(!Settings.canDrawOverlays(this)) {
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivityForResult(intent, 0)
            } else {
                //가능하면 그대로 스크린락 상태가 될때의 동작을 감지할 수 있는 서비스를 시작.
                val intent = Intent(applicationContext, LockScreenService::class.java)
                startForegroundService(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0) {
            // 다른 앱위에 이 앱을 표시할 수 있는지 확인한다. 표시 가능하면 스크린락 상태가 될때의 동작을 감지할 수 있는 서비스를 시작한다.
            if(!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "완", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(applicationContext, LockScreenService::class.java)
                startForegroundService(intent)
            }
        }
    }









}