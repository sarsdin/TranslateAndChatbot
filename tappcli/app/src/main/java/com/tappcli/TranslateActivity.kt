package com.tappcli
import android.util.Log

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.tappcli.databinding.TranslateActivityBinding
import com.tappcli.tr.GoogleTranslationApi
import com.tappcli.tr.GoogleTranslationApiForExport
import com.tappcli.tr.HomeVm
import com.tappcli.tr.LockScreenService
import kotlinx.android.synthetic.main.history_bts.*

class TranslateActivity : AppCompatActivity() {

    val tagName = "[TranslateActivity]"
    lateinit var binding: TranslateActivityBinding
    lateinit var homeVm: HomeVm


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = TranslateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().setNavigationBarColor(Color.BLACK) //하단 소프트바 색상 변경. 투명:TRASPARENT

//        homeVm = ViewModelProvider(this).get(HomeVm::class.java)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_cloud_24)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        Log.e(tagName, "text: $text")

        val isReadOnly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
        Log.e(tagName, "isReadOnly: $isReadOnly")

        val gta = GoogleTranslationApiForExport(this, text.toString())
        val text_result = gta.translate("ko")
        binding.tv.setText(Html.fromHtml("번역: ${text_result}", FROM_HTML_MODE_COMPACT))

//        val intent = Intent()
//        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text_result);
//        setResult(RESULT_OK, intent);


    }


    override fun onResume() {
        super.onResume()



    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)

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




    fun checkPermission() {
        // 마쉬멜로 버전 이상이면 다른 앱위에 표시가능한지 알아보고,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //불가능이면 현재 앱의 패키지명을 이용해 권한을 요청하고 onActivityResult()로 결과를 받는다.
            if(!Settings.canDrawOverlays(this)) {
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == RESULT_OK) {
//            // 다른 앱위에 이 앱을 표시할 수 있는지 확인한다. 표시 가능하면 스크린락 상태가 될때의 동작을 감지할 수 있는 서비스를 시작한다.
//            if(!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "완", Toast.LENGTH_LONG).show()
//            } else {
//                val intent = Intent(applicationContext, LockScreenService::class.java)
//                startForegroundService(intent)
//            }
//        }
    }









}