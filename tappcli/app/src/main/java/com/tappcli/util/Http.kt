package com.tappcli.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object Http {
    //retrofit2 클래스 설정
    //    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";        //DataApi 기본 주소(URL)
    private const val BASE_URL = "http://"
    const val KEY = "AIzaSyCxEDgrF8yd1Vkt-m7zZivxL5cr7nWsIi4" //개발자 키
    const val PART = "snippet" //id, snippet 설정가능한데 snippet 만하면 둘가 가져옴.
    const val MAX_RESULTS = 15
    const val ORDER = "date" //정렬을 날짜순으로
    const val PUBLISHED_AFTER = "2022-02-01T00:00:00Z" //지정된 날짜 이후에 등록된 것만 검색
    const val Q = "premier league highlights" //검색어
    const val TYPE = "video" //비디오타입으로 설정
    const val VIDEO_EMBEDDABLE = true //퍼가기 허용인 것만 검색
    const val CHANNEL_ID_LALIGA = "UCTv-XvfzLX3i4IGWAm4sbmA" //채널 ID - laliga 공식 채널
    const val Q_LALIGA = "highlight" //검색어
    const val UPLOADS_URL = "http://15.165.174.226/uploads/" //검색어

    //    public static final String HOST_IP = "15.165.174.226";                             //서버 ip
//    const val HOST_IP = "127.0.0.1:5001" //서버 ip
    const val HOST_IP = "10.0.2.2:5001" //서버 ip
    const val UNSPLASH_API_URL = "https://api.unsplash.com"

    fun getRetrofitInstance(host: String): Retrofit? {
        // RFC 4627만을 허용할정도로 엄격한 parse 규칙을 사용하지만 setLenient 를 적용하여 완화해줌. (오류전문)--> Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
        val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

        // 로그를 중간에 가로채서 로그캣에 보여줌
//        val interceptor = HttpLoggingInterceptor()
        //        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(/*interceptor*/OkHttpProfilerInterceptor()).build()
        var retrofit: Retrofit? = null
        if (host == HOST_IP) {
            retrofit = Retrofit.Builder()
                .baseUrl("$BASE_URL$host/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        } else if (host == UNSPLASH_API_URL) {
            retrofit = Retrofit.Builder()
                .baseUrl(UNSPLASH_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }



    interface ApiChatBot {
        //채팅 가져오기
//        @Headers("content-type: application/json")
        @GET("getChat")
        fun getChat(
//            @Query("gboard_no") gboard_no: Int,
//            @Query("whereIs") whereIs: String?,
//            @Query("user_no") userNo: Int
        ): Call<JsonObject?>?

        //쓰기
        @Headers("content-type: application/json")
        @POST("writeChat")
        fun writeChat(@Body user_input: JsonObject?): Call<JsonObject?>?

    }

    interface Translate {


        @POST("historyDelete")
        fun 히스토리삭제(@Body user_no: String?, @Body history_no: String?): Call<JsonObject?>?

        @POST("historyDeleteAll/{user_no}")
        fun 히스토리전체삭제(/*@Query*/@Path("user_no") user_no: String?): Call<JsonObject?>?

        @POST("historyFavoriteCheck")
        fun 즐겨찾기등록해제(@Body info: HashMap<String, String>): Call<JsonObject?>?

    }


    interface HttpLogin {
        // 이메일 중복확인 통신
        @GET("joinEmailVerify/{user_email}")
        fun isEmailRedundant(@Path("user_email") user_email: String?): Call<JsonObject?>?

        // 폰번호 중복확인 통신
        @GET("joinPhoneNumberVerify/{user_phone}")
        fun isPhoneNumberRedundant(@Path("user_phone") user_phone: String?): Call<JsonObject?>?

        //회원가입 버튼 클릭시 - 신청
        @POST("joinComplete")
        fun joinComplete(
            @Body joinInfo: HashMap<String, String>
        ): Call<JsonObject?>?

        //로그인 버튼 클릭시
        @POST("/login")
        fun login(
            @Body loginInfo: HashMap<String, String>
        ): Call<JsonObject?>?

        // 기록가져오기
        @POST("/history")
        fun history(
            @Body info: HashMap<String, String>
        ): Call<JsonObject?>?

//        @POST("/historyGet")
//        fun historyGet(
//            @Body info: HashMap<String, String>
//        ): Call<JsonObject?>?

        //아이디 찾기
        @POST("/findId")
        fun findId(
            @Body info: Map<String, String>
        ): Call<JsonObject?>?

        //이메일 번호발송 요청
        @POST("/emailVnumReq")
        fun emailVnumReq(
            @Body info: Map<String, String>
        ): Call<JsonObject?>?

        //이메일 번호인증
        @POST("/vnum")
        fun 번호확인인증(
            @Body vnum: Map<String, String>
        ): Call<JsonObject?>?


        @POST("/newPasswd")
        fun 새비밀번호요청(
            @Body info: Map<String, String>
        ): Call<JsonObject?>?


    }




}