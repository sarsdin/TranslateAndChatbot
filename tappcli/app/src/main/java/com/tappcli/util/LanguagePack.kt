package com.tappcli.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*


// application context 가 필요함
class LanguagePack (val context: Context) {
    companion object {
        private const val ENG = "eng.traineddata"
        private const val KOR = "kor.traineddata"

        fun getDir(context:Context): File {
            return File(context.filesDir, "tesserect")
        }
    }
    init {
        initialize()
    }

    private fun initialize() {
        with(
            getDir(context)
        ) {

            //getDir 로 만든 경로를 이용해 다시 서브경로까지 포함한 경로를 만듦.
            val subdir = File(this, "tessdata")
            //위에서 만든 서브경로를 이용해 또 파일명까지 포함한 세부경로를 만듦. 이것은 파일명까지 적어줌으로써 나중에 언어파일을 io스트림으로 복사하기 위함.
            val engFile = File(subdir, ENG)
            val korFile = File(subdir, KOR)
            if (exists() && subdir.exists() && engFile.exists() && korFile.exists()) {
                return@with
            }
            // 디렉토리가 앱내에 존재하지 않으면 해당 디렉토리를 경로내 차례로 만들어줌
            deleteRecursively()
            mkdir()         // tesserect 디렉토리 생성
            subdir.mkdir() // tesserect/tessdata 디렉토리 생성

            // 위에서 만든 subdir경로내에 복사될 파일을 assets 폴더에서 로드(inputstream열기)한뒤 최종경로까지 전달
            copyFile(context.assets.open(ENG), engFile)
            copyFile(context.assets.open(KOR), korFile)

        }
    }


    /***
     * 언어 데이터 파일, 디바이스에 복사. insteam: 안드로이드 assets 폴더에서 가져온 언어 데이터파일(eng.traineddata)
     * fileName: application 에 포함될 파일의 최종경로
     */
    private fun copyFile(instream: InputStream, fileName: File) {
        try {
//            val filepath: String = datapath.toString() + "파일경로"
//            val assetManager: AssetManager = getAssets()
//            val instream: InputStream = assetManager.open("파일경로")
            //파일이 아웃풋될 경로를 정해서 스트림을 열어줌.
            val outstream: OutputStream = FileOutputStream(fileName)
            val buffer = ByteArray(1024)
            var read: Int
            while (instream.read(buffer).also { read = it } != -1) {
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun printOCRResult(/*context:Context,*/ src:Bitmap, langMode:String): String{
        val mode = if (langMode == "한영") "kor" else "eng"
        with(TessBaseAPI()){
            init(getDir(context).absolutePath, mode) //eng+kor
//            val dst = Mat()
//            Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2RGB)
//            val bitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
//            Utils.matToBitmap(dst, bitmap)
            setImage(src)
            Log.e("LanguagePack","utF8Text :\n$utF8Text")
            return utF8Text
        }
    }

}