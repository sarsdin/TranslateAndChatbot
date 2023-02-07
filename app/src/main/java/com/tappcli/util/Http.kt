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


    interface HttpLogin {
        // 이메일 중복확인 통신
        @GET("home/isEmailRedundant")
        fun isEmailRedundant(@Query("user_email") user_email: String?): Call<JsonObject?>?

        //회원가입 버튼 클릭시 - 신청
        @GET("home/join")
        fun joinComplete(
            @Query("user_email") user_email: String?,
            @Query("user_pwd") user_pwd: String?,
            @Query("user_pwdc") user_pwdc: String?,
            @Query("user_nick") user_nick: String?,
            @Query("user_name") user_name: String?
        ): Call<JsonObject?>?

        //비번찾기 이메일 인증번호 발송 버튼 클릭시 -
        @GET("home/findpwMailSend")
        fun findpwMailSend(
            @Query("user_name") user_name: String?,
            @Query("user_email") user_email: String?
        ): Call<JsonObject?>?

        //비번찾기 인증번호 확인 버튼 클릭시
        @GET("home/findpwMailVnumConfirm")
        fun findpwMailVnumConfirm(
            @Query("findpw_number") findpw_number: String?,
            @Query("name") name: String?,
            @Query("email") email: String?
        ): Call<JsonObject?>?

        //새 비밀번호 설정 완료 버튼 클릭시
        @GET("home/findpwNewPw")
        fun findpwNewPw(
            @Query("user_email") user_email: String?,
            @Query("user_pwd") user_pwd: String?
        ): Call<JsonObject?>?

        //로그인 버튼 클릭시
        //        @GET("home/login")
        //        Call<LoginDto> login(@Query("user_email") String user_email,
        //                             @Query("user_pwd") String user_pwd,
        //                             @Query("user_autologin") Boolean user_autologin
        //        );
        //자동로그인시 쉐어드에 유저정보가 있으면 서버에서 그 유저 정보 불러오면서 자동로그인하기
        //        @GET("home/getAutoLoginInfo")
        //        Call<LoginDto> getAutoLoginInfo(@Query("user_email") String user_email);
        @Multipart
        @POST("home/userProfileImageSelect")
        fun 사용자이미지선택(
            @PartMap userInfo: Map<String?, RequestBody?>?,
            @Part userImage: Part?
        ): Call<JsonObject?>?

        @POST("home/nickModify")
        fun 유저닉네임수정(
            @Query("user_no") user_no: Int,
            @Query("user_nick") user_nick: String?
        ): Call<JsonObject?>?
    }

    interface HttpHome {
        //성경일독
        @GET("bible/getTodayLang")
        fun 성경일독(): Call<JsonObject?>?

        //Unsplash Api 랜덤 이미지 10장
        @Headers(
            "Authorization: Client-ID yEG1lot43cUerBQv3RUNkymPmzAMrXiSxV1mkUGj5JQ",
            "content-type: application/json"
        )
        @GET("/photos/random")
        fun 랜덤이미지(@Query("query") query: String?, @Query("count") count: Int): Call<JsonArray?>?
    }

    interface HttpBible {
        //유저 노트 목록 가져오기
        //        @Headers("content-type: application/json")
        @GET("bible/getNoteList")
        fun getNoteList(@Query("user_no") user_no: Int): Call<JsonArray?>?

        //유저 노트 추가
        @Headers("content-type: application/json")
        @POST("bible/getNoteAdd")
        fun getNoteAdd(@Body noteinfo: JsonObject?): Call<JsonObject?>?

        //유저 노트 수정
        @Headers("content-type: application/json")
        @POST("bible/getNoteUpdate")
        fun getNoteUpdate(@Body noteinfo: JsonObject?): Call<JsonObject?>?

        //유저 노트 삭제
        @Headers("content-type: application/json")
        @POST("bible/deleteNote")
        fun deleteNote(@Query("note_no") note_no: Int): Call<JsonObject?>?
    }

    interface HttpGroup {
        //모임 만들기
        @Multipart
        @POST("group/createGroup")
        fun createGroup(
            @PartMap groupInfo: Map<String?, RequestBody?>?,
            @Part groupImage: Part?
        ): Call<JsonObject?>?

        @Multipart
        @POST("group/groupProfileImageSelect")
        fun 모임이미지선택(
            @PartMap groupInfo: Map<String?, RequestBody?>?,
            @Part groupImage: Part?
        ): Call<JsonObject?>?

        //모임 목록 가져오기
        @Headers("content-type: application/json")
        @POST("group/getGroupL")
        fun getGroupL(
            @Query("user_no") userNo: Int,
            @Query("sortState") sortState: String?
        ): Call<JsonObject?>?

        //모임 상세 가져오기
        @Headers("content-type: application/json")
        @POST("group/getGroupIn")
        fun getGroupIn(
            @Query("group_no") currentGroupIn: Int,
            @Query("sortStateGroupIn") sortStateGroupIn: String?,
            @Query("user_no") userNo: Int
        ): Call<JsonObject?>?

        //모임 게시물 목록 가져오기
        @Headers("content-type: application/json")
        @POST("group/getGroupInL")
        fun getGroupInL(
            @Query("group_no") currentGroupIn: Int,
            @Query("sortStateGroupIn") sortStateGroupIn: String?
        ): Call<JsonObject?>?

        //모임 글쓰기
        @Multipart
        @POST("group/writeGroupIn")
        fun writeGroupIn(
            @PartMap writeInfo: Map<String?, RequestBody?>?,
            @Part writeImage: List<Part?>?
        ): Call<JsonObject?>?

        //모임 글수정
        @Multipart
        @POST("group/updateBoardGroupIn")
        fun updateBoardGroupIn(
            @PartMap updateInfo: Map<String?, RequestBody?>?,
            @Part updateImage: List<Part?>?
        ): Call<JsonObject?>?

        //모임 글삭제
        @Headers("content-type: application/json")
        @POST("group/deleteBoardGroupIn")
        fun deleteBoardGroupIn(
            @Query("gboard_no") gboard_no: Int,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        //모임 글 상세 가져오기
        @Headers("content-type: application/json")
        @POST("group/getGboardDetail")
        fun getGboardDetail(
            @Query("gboard_no") gboard_no: Int,
            @Query("whereIs") whereIs: String?,
            @Query("user_no") userNo: Int
        ): Call<JsonObject?>?

        //모임 댓글 쓰기
        @Headers("content-type: application/json")
        @POST("group/writeGboardReply")
        fun writeGboardReply(@Body replyInfo: JsonObject?): Call<JsonObject?>?

        //모임 댓글 삭제
        @Headers("content-type: application/json")
        @POST("group/deleteGboardReply")
        fun deleteGboardReply(@Body replyInfo: JsonObject?): Call<JsonObject?>?

        //모임 댓글 수정
        @Headers("content-type: application/json")
        @POST("group/modifyGboardReply")
        fun modifyGboardReply(@Body replyInfo: JsonObject?): Call<JsonObject?>?

        //모임 좋아요 클릭 - insert or delete
        @Headers("content-type: application/json")
        @POST("group/clickGboardLike")
        fun clickGboardLike(
            @Query("gboard_no") gboard_no: Int,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        //챌린지만들기총분량수계산
        @Headers("content-type: application/json")
        @POST("group/getCountVerseForChalCreate")
        fun 챌린지만들기총분량수계산(@Body params: JsonArray?): Call<JsonObject?>?

        //챌린지만들기완료하기
        @Headers("content-type: application/json")
        @POST("group/createChallenge")
        fun 챌린지만들기완료하기(@Body params: JsonObject?): Call<JsonObject?>?

        //챌린지목록가져오기
        @Headers("content-type: application/json")
        @POST("group/getChallengeList")
        fun 챌린지목록가져오기(
            @Query("user_no") user_no: Int,
            @Query("group_no") group_no: Int
        ): Call<JsonObject?>?

        //챌린지상세목록가져오기
        @Headers("content-type: application/json")
        @POST("group/getChallengeDetailList")
        fun 챌린지상세목록가져오기(
            @Query("chal_no") chal_no: Int,
            @Query("user_no") user_no: Int,
            @Query("group_no") group_no: Int
        ): Call<JsonObject?>?

        @Headers("content-type: application/json")
        @POST("group/getChallengeDetailVerseList")
        fun 챌린지인증진행하기(@Body params: JsonObject?): Call<JsonObject?>?

        @Headers("content-type: application/json")
        @POST("group/updateChallengeDetailVerse")
        fun 챌린지인증체크업데이트(@Body params: JsonObject?): Call<JsonObject?>?

        @Multipart
        @POST("group/createChalDetailVideo")
        fun 챌린지인증영상업로드(
            @PartMap groupInfo: Map<String?, RequestBody?>?,
            @Part groupImage: Part?
        ): Call<JsonObject?>?

        @POST("group/createChalDetailVideoFirstWork")
        fun 챌린지인증영상업로드사전작업(@Query("chal_detail_no") chal_detail_no: Int): Call<JsonObject?>?

        @POST("group/chalLikeClicked")
        fun 챌린지상세좋아요클릭(@Body params: JsonObject?): Call<JsonObject?>?

        //////////////모임 초대 관련
        @POST("group/isInviteNumber")
        fun 모임초대번호있는지확인(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberInviteNumber")
        fun 모임초대번호재생성요청(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberInviteLink")
        fun 모임초대링크생성(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberInviteLinkConfirm")
        fun 모임초대링크유효한지확인(
            @Query("group_no") group_no: Int,
            @Query("invite_code") invite_code: String?,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        @POST("group/memberInviteLinkMemberAdd")
        fun 모임초대링크로멤버추가하기(
            @Query("group_no") group_no: Int,
            @Query("invite_code") invite_code: String?,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        @POST("group/memberInviteNumberMemberAdd")
        fun 모임초대번호로멤버추가하기( /*@Query("group_no") int group_no,*/
            @Query("invite_code") invite_code: String?,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        @POST("group/memberInviteNumberVerify")
        fun 모임초대번호로멤버추가하기전유효성확인( /*@Query("group_no") int group_no,*/
            @Query("invite_code") invite_code: String?,
            @Query("user_no") user_no: Int
        ): Call<JsonObject?>?

        @POST("group/memberListLoad")
        fun 모임멤버목록로드(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberOut")
        fun 모임멤버탈퇴(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberFire")
        fun 모임멤버추방(@Body params: JsonObject?): Call<JsonObject?>?

        @POST("group/memberListSearch")
        fun 모임멤버검색(@Body params: JsonObject?): Call<JsonObject?>?

        //채팅방 만들기 - 만들기 후 채팅방 정보+참가자목록 가져옴
        @Multipart
        @POST("group/chatRoomCreate")
        fun 채팅방만들기(
            @PartMap chatRoomInfo: Map<String?, RequestBody?>?,
            @Part chatRoomImage: Part?
        ): Call<JsonObject?>?

        //채팅방 목록 가져오기
        @POST("group/chatRoomList")
        fun 채팅방목록(@Body params: JsonObject?): Call<JsonObject?>?

        //채팅방 정보 및 채팅 내역 가져오기 - 채팅방 정보+참가자목록, 채팅 내역 목록+채팅쓴사람
        @POST("group/chatRoomJoin")
        fun 채팅방참가클릭(
            @Query("chat_room_no") chat_room_no: Int,
            @Query("user_no") user_no: Int,
            @Query("group_no") group_no: Int
        ): Call<JsonObject?>?

        @Multipart
        @POST("group/chatRoomUploadImages")
        fun 채팅방이미지업로드클릭(
            @PartMap chatInfo: Map<String?, RequestBody?>?,
            @Part uploadImages: List<Part?>?
        ): Call<JsonObject?>?
    }
}