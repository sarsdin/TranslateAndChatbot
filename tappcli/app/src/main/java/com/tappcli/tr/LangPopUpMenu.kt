package com.tappcli.tr
import android.util.Log

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity

class LangPopUpMenu(val homeVm: HomeVm, val context: FragmentActivity, val anchor: View,
                     val menuId: Int,
                    /*gravity: Int*/) : PopupMenu(context.applicationContext, anchor/*, gravity*/){

    val tagName = "[${this.javaClass.simpleName}]"
//    var context: Context? = null
    init {
//        this.context = context
//        팝업메뉴()
    }

    /**
     * parameters : start or end
     * */
    fun 팝업메뉴(startOrEnd: String){
        //팝업 메뉴 생성 후 각 메뉴에 대한 xml 파일 인플레이트
        context.menuInflater.inflate(menuId, menu)
        Log.i(tagName,"context: ${context.title}")
        //각 메뉴항목 클릭했을때의 동작 설정
        setOnMenuItemClickListener { menuItem ->

            menuItem.itemId
            val selectedLangCode = menuItem.titleCondensed.toString()       //구글번역 코드용도
            val selectedLangTitle = menuItem.title.toString()               //UI 용도
            val selectedLangTtsCode = menuItem.contentDescription.toString() //구글 tts 용도
            val select = arrayOf(selectedLangCode, selectedLangTitle, selectedLangTtsCode)

            Log.i(tagName,"select: ${select.contentToString()}")

            // 언어종류변수1, 언어종류변수2 필요. 선택된 언어에 해당하는 번역이 실행되어야함. 메뉴에 따른 번역언어 선택의 자동화가 필요함.
            val 이전변수1의값 = homeVm.liveCurrentLangStart.value!!
            val 이전변수2의값 = homeVm.liveCurrentLangEnd.value!!

            if(startOrEnd == "start"){
                //선택한 변수가 변수2의 값과 같은지 확인하고 같으면, 변수2의 값을 이전에 있던 변수1의 값으로 변경한다.
                //그리고, 현재 선택된 값을 변수1에 넣는다.
                if(select[0] == 이전변수2의값[0]){
                    homeVm.liveCurrentLangEnd.value = 이전변수1의값
                }
                homeVm.liveCurrentLangStart.value = select




            }else{
                if(select[0] == 이전변수1의값[0]){
                    homeVm.liveCurrentLangStart.value = 이전변수2의값
                }
                homeVm.liveCurrentLangEnd.value = select
            }


            return@setOnMenuItemClickListener true
        }

    }






}