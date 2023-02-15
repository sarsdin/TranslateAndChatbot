package com.tappcli.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.suspendCancellableCoroutine

class AlertDialogCustom(val context: Context, val title:String, val msg:String) {

    suspend fun show():Boolean {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(msg)
//                .setNegativeButton("취소") { dialog, which ->
//                    res = false
//                }
//                .setPositiveButton("확인") { dialogInterface, i ->
//                    res = true
//                }
            .create()
            .await(positiveText = "확인", negativeText = "취소")
    }

    suspend fun AlertDialog.await(
        positiveText: String,
        negativeText: String
    ) = suspendCancellableCoroutine<Boolean> { continuation ->
        //suspendCancellableCoroutine 로 코루틴 잠시 중단하고 그 코루틴을 블럭에 전달함. 전달된 코루틴은 신속한 취소가 가능하다고 한다.
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == AlertDialog.BUTTON_POSITIVE) continuation.resume(true, onCancellation = null)
            else if (which == AlertDialog.BUTTON_NEGATIVE) continuation.resume(false, onCancellation = null)
        }

        setButton(AlertDialog.BUTTON_POSITIVE, positiveText, listener)
        setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, listener)

        // we can either decide to cancel the coroutine if the dialog
        // itself gets cancelled, or resume the coroutine with the
        // value [false]
        //다이얼로그 취소시 현재 코루틴을 취소시킴.
        setOnCancelListener { continuation.cancel() }

        // if we make this coroutine cancellable, we should also close the
        // dialog when the coroutine is cancelled
        //코루틴 취소되면 창도 끔
        continuation.invokeOnCancellation { dismiss() }

        // remember to show the dialog before returning from the block,
        // you won't be able to do it after this function is called!
        show()
    }



}