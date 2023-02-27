package com.tappcli.tr

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.tappcli.MainActivity
import com.tappcli.MyApp
import com.tappcli.R
import com.tappcli.databinding.FavoriteVhBinding
import com.tappcli.util.Http
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavoriteFmRva(val homeVm: HomeVm, val activity: MainActivity) : RecyclerView.Adapter<FavoriteFmRva.FavoriteRvaVh>() {
    val tagName = "[FavoriteFmRva]"

         // This property is only valid between onCreateView and onDestroyView.
//         private val binding get() = _binding!!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRvaVh {
        return FavoriteRvaVh(FavoriteVhBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteRvaVh, position: Int) {
        holder.bind(homeVm.liveFavoriteL.value?.get(position) as JsonObject)
    }

    override fun getItemCount(): Int {
        return homeVm.liveFavoriteL.value!!.size()
    }

    override fun onViewAttachedToWindow(holder: FavoriteRvaVh) {
        holder.setIsRecyclable(false) //홀더뷰 재사용 안함으로 설정하여 홀더의 절 ui들이 독립적으로 작동하게 함
        super.onViewAttachedToWindow(holder)
//        Log.e("[BibleVerseBtsRva]", "onViewAttachedToWindow "+ holder.getAbsoluteAdapterPosition());
    }

    inner class FavoriteRvaVh internal constructor(binding: FavoriteVhBinding): RecyclerView.ViewHolder(binding.root) {
        private val vhBinding: FavoriteVhBinding = binding
        private lateinit var mItem: JsonObject


        fun bind(mItem: JsonObject) {
            this@FavoriteRvaVh.mItem = mItem

            vhBinding.tv.text = MyApp.getTime(".ui", mItem.get("favorite_create_date").asString)
            vhBinding.tv2.text = mItem.get("favorite_content").asString
            vhBinding.tv3.text = mItem.get("favorite_content_result").asString
            if(mItem["favorite_no"].asString == "0"){
                val di: Drawable? = ResourcesCompat.getDrawable(
                    MyApp.getApplication().resources,
                    R.drawable.ic_baseline_star_border_24, null)
                vhBinding.bt.setImageDrawable(di)

            }else{
                val di: Drawable? = ResourcesCompat.getDrawable(MyApp.application.resources,
                    R.drawable.ic_baseline_star_24, null)
                vhBinding.bt.setImageDrawable(di)
            }

            //개별 즐찾 or 히스토리 클릭시 재번역하기 - 즐찾 or 히스토리에서 클릭했을시 api요청 하지 말기 처리
            vhBinding.root.setOnClickListener {
                activity.findNavController(R.id.nav_host_fragment_main_activity)
                    .navigate(R.id.action_global_resultFm, Bundle().apply bk@ {
                        putString("signal1", MyApp.히스토리_및_즐겨찾기에서_왔음.toString())
                        putString("data", mItem.toString())
                    })
                activity.binding.navView.checkedItem?.let { it.isChecked = false }
            }



            //개별 히스토리 즐겨찾기 등록/해제
            //1. 안드로이드 요청만들기 / 서버 요청 api 만들기 - favoriteCreate / favoriteDelete
            //2. 해제시 서버 요청에서 즐찾번호를 이용해서 히스토리테이블 업데이트(즐찾번호삭제해줌)함.
            //3. 등록시 등록api(create)에서 히스토리번호를 이용해 즐찾create 먼저 해주고, 해당 히스토리번호로 히스토리테이블 업데이트(즐찾번호추가)함.
            vhBinding.bt.setOnClickListener {
                즐겨찾기등록해제()
            }



            //즐겨찾기
            //1. 즐찾 ui - fm하나 만들어서 거기 데이터 넣기
            //2. api는 히스토리 번호를 넣어서 요청함. 서버에서는 히스토리 번호의 유무를 if한뒤 히스토리 테이블도 update작업.




        }//bind() 끝 부분



        private fun 즐겨찾기등록해제() {
            val infoMap = HashMap<kotlin.String, kotlin.String>()
            infoMap["user_no"] = MyApp.id.toString()
            infoMap["user_name"] = MyApp.nick.toString()
            infoMap["user_email"] =  MyApp.email.toString()
            infoMap["history_no"] =  mItem["history_no"].asString
//            infoMap["history_content"] =  mItem["history_content"].asString
//            infoMap["history_content_result"] =  mItem["history_content_result"].asString
            infoMap["favorite_no"] =  mItem["favorite_no"].asString //null 여부에 따른 이상

            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.Translate::class.java)
                .즐겨찾기등록해제(infoMap)!!.enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {

                        Log.e(tagName, "즐겨찾기등록해제 onResponse: 통신성공, code는: " + response.code())
                        if (response.isSuccessful) {
                            val res = response.body()
                            if (res!!["res"].asBoolean) {
                                homeVm.liveHistoryL.value = res["histories"].asJsonArray
                                homeVm.liveFavoriteL.value = res["favorites"].asJsonArray

                            } else {
//                            ToastUtil.showToast("회원정보가 맞지 않습니다.")
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        Log.e(tagName, "즐겨찾기등록해제 onFailure: " + t.message)
                    }
                })
        }

        private fun 히스토리삭제() {
            Http.getRetrofitInstance(Http.HOST_IP)!!.create(Http.Translate::class.java)
            .히스토리삭제(MyApp.id.toString(), mItem.get("history_no").asString)!!
            .enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {

                    Log.e(tagName, "히스토리삭제 onResponse: 통신성공, code는: " + response.code())
                    if (response.isSuccessful) {
                        val res = response.body()
                        if (res!!["res"].asBoolean) {
                            homeVm.liveHistoryL.value = res["histories"].asJsonArray

                        } else {
//                            ToastUtil.showToast("회원정보가 맞지 않습니다.")
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e(tagName, "히스토리삭제 onFailure: " + t.message)
                }
            })
        }




    }

}




//"res" : "true",
//"histories" : [ {
//    "user_no" : 1,
//    "user_name" : "test",
//    "history_content_result" : "test",
//    "favorite_no" : 0,
//    "history_content_lang" : "ko",
//    "history_content" : "test",
//    "user_email" : "test@test.com",
//    "history_no" : 5,
//    "history_create_date" : "2023-02-21T13:05:36",
//    "history_content_result_lang" : "en"
//}, {

//favorite_no = Column(Integer, primary_key=True, autoincrement=True, comment='즐겨찾기 번호')
//user_no = Column(Integer, ForeignKey("User.user_no"), nullable=True, comment='사용자 번호')
//user_email = Column(String(50), nullable=True, comment='사용자 이메일(ID)')
//user_name = Column(String(50), nullable=True, comment='사용자 이름')
//favorite_content = Column(Text, nullable=True, comment='즐겨찾기 내용')
//favorite_content_result = Column(Text, nullable=True, comment='즐겨찾기 번역 결과')
//favorite_create_date = Column(DateTime, nullable=True, comment='즐겨찾기 생성시간')
//history_no = Column(Integer, nullable=True, comment='히스토리 번호')
