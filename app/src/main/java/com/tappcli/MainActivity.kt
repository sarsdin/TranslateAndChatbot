package com.tappcli

import android.graphics.Color
import android.os.Bundle
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
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tappcli.databinding.MainActivityBinding

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
        binding.navView.setupWithNavController(navController) //네비게이션 컨트롤러와 연동하여 상태저장 및 복원 관리

        binding.toolbar.setNavigationOnClickListener {
            binding.root.open()
        }

        binding.navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
//                item.setChecked(false)
                binding.root.close()
                val id = item.itemId
                val title = item.title.toString()

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

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
//        }
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
}