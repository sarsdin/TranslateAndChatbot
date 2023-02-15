package com.tappcli

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity.LEFT
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tappcli.databinding.MainActivityBinding
import com.tappcli.tr.LockScreenService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().setNavigationBarColor(Color.BLACK) //하단 소프트바 색상 변경. 투명:TRASPARENT

//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_cloud_24)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        //fragmentcontainerview 사용시 supportFMmanager를 이용하여 컨트롤러를 등록해야 illegalerror가 안뜸
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main_activity) as NavHostFragment
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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