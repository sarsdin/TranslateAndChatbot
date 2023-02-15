package com.tappcli.tr

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tappcli.R
import com.arthurivanets.bottomsheets.BaseBottomSheet
import com.arthurivanets.bottomsheets.BottomSheet
import com.arthurivanets.bottomsheets.config.BaseConfig
import com.arthurivanets.bottomsheets.config.Config
import com.tappcli.databinding.LayoutRecordBottomSheetBinding

/**
 * 바텀시트 다이얼로그 뷰
 * 바텀시트로 다이얼로그가 뜬다.
 * */
class RecordBottomSheetDialog(hostActivity: Activity,config: BaseConfig = Config.Builder(hostActivity).build())
    :BaseBottomSheet(hostActivity, config) {

    lateinit var binding: LayoutRecordBottomSheetBinding

    override fun onCreateSheetContentView(context: Context): View {
        binding = LayoutRecordBottomSheetBinding.inflate(LayoutInflater.from(context), this, false)
//         binding1  = DataBindingUtil.inflate(LayoutInflater.from(context),
//            R.layout.layout_record_bottom_sheet,
//            this,
//            false
//        )
//        (binding1 as LayoutRecordBottomSheetBinding).closeButton.

//        val root /*: ConstraintLayout*/ = LayoutInflater.from(context)
//            .inflate(R.layout.layout_record_bottom_sheet,this,false) /*as ConstraintLayout*/

        // 닫기 버튼 누르면 dismiss()
//        binding.closeButton.setOnClickListener {
//            dismiss()
//        }
//        val bt1 = root.findViewById<ImageView>(R.id.close_button)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        //바텀시트 해제시 실행
        setOnDismissListener(object :BottomSheet.OnDismissListener{
            override fun onDismiss(bottomSheet: BottomSheet) {

            }
        })

        return binding.root
    }



}