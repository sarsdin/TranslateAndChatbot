package com.tappcli.util

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

/**
 * 셀렉트 텍스트시 나오는 박스의 메뉴에 새로운 메뉴를 추가한다.
 * 셀렉트 텍스트 박스가 나왔을때, 노출될 다른 프로그램의 activity 들을 수동으로 query하여 가져오는 유틸클래스.
 * */
class ContextMenuLoad(var tagNameP:String, var view: View) {

    var tagName : String = tagNameP + " - ${ this.javaClass.canonicalName }"
    var binding: View = view


    fun onInitializeMenu(menu: Menu, compareForChangeLabel: Map<String, String>) {
        // Start with a menu Item order value that is high enough
        // so that your "PROCESS_TEXT" menu items appear after the
        // standard selection menu items like Cut, Copy, Paste.
        Log.e(tagName, "onInitializeMenu getSupportedActivities(): ${getSupportedActivities()}")
        var menuItemOrder = 100
        for (resolveInfo in getSupportedActivities()!!) {
            Log.e(tagName, "resolveInfo.loadLabel(): ${interceptLoadLabel(
                resolveInfo,
                compareForChangeLabel
            )}")
            menu.add(
                Menu.NONE, Menu.NONE,
                /*menuItemOrder++,*/Menu.NONE,
                interceptLoadLabel(resolveInfo, compareForChangeLabel)
            )
                .setIntent(createProcessTextIntentForResolveInfo(resolveInfo!!)) //메뉴를 클릭했을때 동작할 인텐트를 지정함.
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
    }

    private fun interceptLoadLabel(resolveInfo: ResolveInfo, compareForChangeLabel: Map<String, String>): String {
        var res = ""
        for(compareString in compareForChangeLabel){
            val tmpString = resolveInfo.loadLabel(binding.getContext().getPackageManager())
            if(tmpString == compareString.key){
                res = compareString.value
            }
        }
        return res
    }


    /**
     * 메뉴에서 뜨는 명령 클릭시 -
     * 뷰에서 선택된 텍스트를 다룰 수 있는(처리할 수 있는) activity들에게 요청할 수 있는 인텐트를 생성함.
    * */
    private fun createProcessTextIntent(): Intent? {
        return Intent()
            .setAction(Intent.ACTION_PROCESS_TEXT)
            .setType("text/plain")
    }

    /**
     * 해당 뷰에 속한 컨텍스트를 이용하여 패키지매니저를 불러온다. 그리고,
     * 그것을 이용해 필요한 인텐트 action을 사용하여 해당되는 activity들의 정보를 List로 반환함. */
    private fun getSupportedActivities(): List<ResolveInfo>? {
//        val packageManager: PackageManager = activity?.packageManager!!
        val packageManager: PackageManager = binding.getContext().getPackageManager()
        Log.e(tagName, "getSupportedActivities() packageManager: ${packageManager}")
        return packageManager.queryIntentActivities( // << 인텐트 지정시 AndroidManifest.xml - <queries> 태그에 지정된 <intent>만 사용가능.
            createProcessTextIntent()!!,
//            PackageManager.MATCH_ALL/*0*/
            PackageManager.MATCH_DEFAULT_ONLY
        )
    }

    /**
     * 메뉴를 클릭했을때 동작할 인텐트를 지정함.
     * */
    private fun createProcessTextIntentForResolveInfo(info: ResolveInfo): Intent? {
        return createProcessTextIntent()
            //셀렉트된 텍스트를 읽기전용이냐 아니냐를 지정 + 그 텍스트를 인텐트에 첨부해 보냄.
            ?.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            ?.setClassName(
                info.activityInfo.packageName,
                info.activityInfo.name
            )
    }



}