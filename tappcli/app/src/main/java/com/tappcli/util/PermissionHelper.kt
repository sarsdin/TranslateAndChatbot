package com.tappcli.util

import android.Manifest
import android.app.Activity

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.tappcli.MainActivity


object PermissionHelper {
    const val PERMISSION_LOCATION_REQUEST_CODE = 100
    const val PERMISSION_RECORD_REQUEST_CODE = 200

    val REQUIRED_PERMISSION_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUIRED_PERMISSION_RECORD = arrayOf(Manifest.permission.RECORD_AUDIO)

    fun hasGPSPermission(activity: MainActivity): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            REQUIRED_PERMISSION_LOCATION[0]
        )

        // 권한을 허용한 상태라면 (Android 6.0 이하는 위치권한 허용 필요 없음)
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    // GPS 활성화를 확인하기 위한 펄미션 체크
    fun checkGPSPermission(activity: Activity, granted: () -> Unit) {
        /*
        - 런타임 퍼미션 처리
        - 위치 권한을 허용했는지 여부를 확인
         */
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            REQUIRED_PERMISSION_LOCATION[0]
        )

        // 권한을 허용한 상태라면 (Android 6.0 이하는 위치권한 허용 필요 없음)
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 권한 허용이 승인됐다는 콜백 수행
            granted()
        } else { // 권한을 허용하지 않은 상태라면
            // 이전에 권한을 거부한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, REQUIRED_PERMISSION_LOCATION[0])) {
                // 권한 요청이 필요하다는 Toast를 보여줌.
//                activity.showToast(App.context?.getString(R.string.permission_location_alert, App.context?.getString(R.string.app_name))!!)
                Toast.makeText(activity, "퍼미션이 없습니다.",Toast.LENGTH_SHORT).show()
                // 사용자게에 펄미션 요청, 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(
                    activity, REQUIRED_PERMISSION_LOCATION,
                    PERMISSION_LOCATION_REQUEST_CODE
                )
            } else { // 처음 권한 허용을 요청받은 경우
                // 사용자게에 펄미션 요청, 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(
                    activity, REQUIRED_PERMISSION_LOCATION,
                    PERMISSION_LOCATION_REQUEST_CODE
                )
            }
        }
    }

    // 오디오 활성화를 확인하기 위한 펄미션 체크
    fun checkRecordPermission(activity: Activity, granted: () -> Unit) {
        /*
        - 런타임 퍼미션 처리
        - 오디오 레코드 권한을 허용했는지 여부를 확인
         */
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(activity, REQUIRED_PERMISSION_RECORD[0])

        // 권한을 허용한 상태라면 (Android 6.0 이하는 위치권한 허용 필요 없음)
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 권한 허용이 승인됐다는 콜백 수행
            granted()
        } else { // 권한을 허용하지 않은 상태라면
            // 권한 요청이 필요하다는 Toast를 보여줌.
            // 사용자게에 펄미션 요청, 요청 결과는 onRequestPermissionResult에서 수신
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSION_RECORD, PERMISSION_RECORD_REQUEST_CODE)
        }
    }

}