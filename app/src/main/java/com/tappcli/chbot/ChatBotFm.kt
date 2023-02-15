package com.tappcli.chbot

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tappcli.databinding.ChatbotFmBinding
import com.tappcli.util.Http
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatBotFm : Fragment() {
    val tagName = "[ChatBotFm]"
    val host = "10.0.2.2:5001" //안드로이드는 127.0.0.1에 접근 불가. 대신 미리 지정된 alias를 사용해 접근가능.
    lateinit var chatBotVm: LoginVm
    var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    lateinit var rv: RecyclerView
    lateinit var rva: ChatBotRva
//    lateinit var imgVpa: HomeFmImgVpa

//    lateinit var tops: TopSheetBehavior<LinearLayout>

    var mbinding: ChatbotFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBotVm = ViewModelProvider(requireActivity()).get(LoginVm::class.java)
        채팅가져오기("getChat")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mbinding = ChatbotFmBinding.inflate(inflater, container, false)
        Log.e(tagName, "왜 안돼냐!?? ")

        rv = binding.chatList
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = ChatBotRva(chatBotVm, this)
        rva = rv.adapter as ChatBotRva

        return binding.root
    }

    //editImageActivity 를 열었던 결과를 콜백받음.
    val editImageActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if(resultCode == Activity.RESULT_OK){
//            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(UnsplashPickerActivity.EXTRA_PHOTOS)
//            if (photos != null) {
//                homeVm.unsplashL = photos
//                homeVm.liveUnsplashL.value = homeVm.unsplashL
//            }
            if (data != null) {
                Log.e(tagName, "editImageActivityForResult: ${data.getStringExtra("editFinished")}")
            } else {
                Log.e(tagName, "editImageActivityForResult: data is null")
            }
        }
    }

    //unsplash picker Activity 를 열었던 결과를 콜백받음.
//    val unsplashForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
//        val resultCode = result.resultCode
//        val data = result.data
//
//        if(resultCode == Activity.RESULT_OK){
//            val photos : ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(
//                UnsplashPickerActivity.EXTRA_PHOTOS)
//            if (photos != null) {
//                homeVm.unsplashL = photos
//                homeVm.liveUnsplashL.value = homeVm.unsplashL
//            }
//        }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        appBarConfiguration = AppBarConfiguration.Builder(R.id.home_fm).build()
//        navController = Navigation.findNavController(view)
//        NavigationUI.setupWithNavController(
//            binding.homeToolbar,
//            navController!!,
//            appBarConfiguration!!
//        )

        //뒤로가기 버튼 클릭시 뒤로
        binding.toolbarSlide.setOnClickListener {
            findNavController().navigateUp()
        }

//        채팅가져오기()

        //채팅 리사이클러뷰 갱신
        chatBotVm.liveChatL.observe(viewLifecycleOwner, Observer {
            rva.notifyDataSetChanged()
            스크롤컨트롤()
        })

        //채팅 쓰기
        binding.sendIbt.setOnClickListener {
            val userInput = binding.inputEt.text.toString()
            if (userInput == "") return@setOnClickListener

            val userInput2 = JsonObject()
            userInput2.addProperty("user_input", userInput)
            채팅가져오기("writeChat", userInput2)

            //키보드 초기화 및 내리기
            스크롤컨트롤()
            binding.inputEt.setText("")
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputEt.windowToken, 0)

        }

        binding.inputEt.setOnClickListener {
            스크롤컨트롤()
        }









    }

    fun 스크롤컨트롤(){
        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
            rv.scrollToPosition(rv.adapter!!.itemCount - 1)
//                rv.scrollToPosition(chatBotVm.chatL.size()-1)
        },100)
    }

    fun 채팅가져오기(flag: String, userInput: JsonObject? = JsonObject()){
        Log.e("[ChatBotVm]", "챗봇 onResponse: test용")
        val retrofit = Http.getRetrofitInstance(host)
        val httpGroup = retrofit!!.create(Http.ApiChatBot::class.java) // 통신 구현체 생성(미리 보낼 쿼리스트링 설정해두는거)
        var call : Call<JsonObject?>? = null
        if(flag == "getChat"){
            call = httpGroup.getChat()
        }else if(flag == "writeChat"){
            call = httpGroup.writeChat(userInput)
        }
        call?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.e("[ChatBotVm]", "챗봇 onResponse: $res")

                    val past_user_inputs = res.get("past_user_inputs").asJsonArray
                    val generated_responses = res.get("generated_responses").asJsonArray
                    val tmpL = JsonArray()
                    past_user_inputs.forEachIndexed { i, je ->
                        tmpL.add(je)
                        tmpL.add(generated_responses[i])
                    }

                    chatBotVm.chatL = tmpL
                    Log.e(tagName, "chatBotVm.chatL: ${chatBotVm.chatL}")
                    chatBotVm.liveChatL.value = chatBotVm.chatL
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("[ChatBotVm]", "챗봇 onFailure: " + t.message)
            }
        })
    }






    override fun onResume() {
        super.onResume()
//        채팅가져오기("writeChat")
//        rva.notifyDataSetChanged()
        스크롤컨트롤()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }


}