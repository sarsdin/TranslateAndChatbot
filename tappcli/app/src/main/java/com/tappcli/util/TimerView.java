package com.tappcli.util;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textview.MaterialTextView;

public class TimerView extends MaterialTextView {

    private ObjectAnimator animator;
    //인증시간이 지나면 false 로 바뀌는 라이브데이터. 뷰에서 observe()를 이용해 감지하여 코드를 짜면된다.
    public MutableLiveData<Boolean> mCertification = new MutableLiveData<>();
    private int time;

    //인증가능한 시간제한안인지 여부
    //사실 certification 변수는 이제 mCertification 으로 대체되서 필요가 없다.
//    private boolean certification = false;

    public TimerView(Context context ) {
        super( context );
    }

    public TimerView(Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    public void start( int durationTime ) {
        mCertification.setValue(true);
        setTime( durationTime );
//        setCertification( true );
        animator = ObjectAnimator.ofInt( this, "time", 0 );
        animator.setDuration( durationTime );
        animator.setInterpolator( new LinearInterpolator( ) );
        animator.start( );
    }

//    public void stop( ) {
//        animator.cancel( );
//        setTime( 0 );
//        setCertification( false );
//        mCertification.setValue(false);
//    }
    public void setDefault( ) {
        if(animator != null){
            animator.cancel();
        }
        setTime( 0 );
//        setCertification( false );
        mCertification.setValue(false);
    }

//    public boolean isCertification( ) {
//        return certification;
//    }

//    private void setCertification( boolean certification ) {
//        this.certification = certification;
//    }

    public int getTime( ) {
        return time;
    }


    //이 메소드는 ObjectAnimator.ofInt() 메소드에 의해 사용되는듯
    // 3분 == 180000, 1분 == 60000
    public void setTime( int time ) {
        this.time = time;

        int h = time / 3600000;
        int m = ( time - h * 3600000 ) / 60000;
        int s = ( time - h * 3600000 - m * 60000 ) / 1000;

        //주석해제시 format 형식 : 00:00:00

//        String hh = h < 10 ? "0"+h: h+"";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
//        this.setText(hh+":"+mm+":"+ss);
        this.setText( mm + ":" + ss );

        if ( this.time == 0 ) {
//            setCertification( false ); //사실 certification 변수는 이제 mCertification 으로 대체되서 필요가 없다.
            mCertification.setValue(false);
        }
    }


}