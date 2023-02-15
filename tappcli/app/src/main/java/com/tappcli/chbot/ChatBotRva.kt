package com.tappcli.chbot

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.gson.JsonPrimitive
import com.tappcli.databinding.ChatbotFmVhBinding
import com.tappcli.databinding.ChatbotFmVhLeftBinding

class ChatBotRva(val chatBotVm: LoginVm, val chatBotFm: ChatBotFm) : RecyclerView.Adapter<ChatBotRva.ChatBotFmVh>() {
    val tagName = "[ChatBotRva]"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotRva.ChatBotFmVh {
        return if(viewType == 0){
            //1이 아니면 다른 사용자용 뷰홀더 표시
            ChatBotFmVh(ChatbotFmVhLeftBinding.inflate(LayoutInflater.from(parent.context), parent,false))
        } else {
            //1이면 자신의 채팅을 보여주는 뷰홀더 표시
            ChatBotFmVh(ChatbotFmVhBinding.inflate(LayoutInflater.from(parent.context), parent,false))
        }
    }


    override fun onBindViewHolder(holder: ChatBotRva.ChatBotFmVh, position: Int) {
        holder.bind(chatBotVm.chatL[position] as JsonPrimitive)
    }


    override fun onViewAttachedToWindow(holder: ChatBotFmVh) {
        holder.setIsRecyclable(false)
        super.onViewAttachedToWindow(holder)
    }


    override fun getItemCount(): Int {
        if(chatBotVm.chatL.isJsonNull || chatBotVm.chatL.size() == 0){
            return 0
        }
        return chatBotVm.chatL.size()
    }


    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        //채팅 리스트가 존재하고, 그 채팅 요소의 글쓴이가 현재 앱의 사용자의 번호와 같으면 나의 채팅으로 간주하고 오른쪽에 표시되는 뷰홀더를 선택한다!
        if (chatBotVm.chatL.size() != 0) {
            //최초 접속시 chat_type 을 접속알림이라고 정해서 보내고 다시 받은 메시지가 chatL 에 추가되어
            // 뷰홀더를 갱신하면 2번 viewType 을 인플레이트해야한다. 최초 방에 접속시에만 알리고 그후 방에서 나가기를 하지 않으면
            // 다름 접속시부터는 알리지 않는다.
            return if(position%2 == 0){
                1   //짝수번 인덱스일때 : 봇의 채팅
            } else {
                0   //홀수번 인덱스일때 : 자신의 채팅
            }
        }
        return 0
    }



    inner class ChatBotFmVh(var mBinding: ViewBinding) : RecyclerView.ViewHolder(mBinding.root) {
//        var rv: RecyclerView? = null

        private lateinit var lBinding: ChatbotFmVhLeftBinding
        private lateinit var rBinding: ChatbotFmVhBinding

        init {
        }


        //mItem -- chatL
        fun bind(mItem: JsonPrimitive) {
//            this.mItem = mItem;

            //봇의 뷰홀더일때
            if (mBinding is ChatbotFmVhLeftBinding){
                lBinding = mBinding as ChatbotFmVhLeftBinding
                if(absoluteAdapterPosition < 6){
                    lBinding.constraintLayout3.visibility = View.GONE
                }

//                ImageHelper.getImageUsingGlide(chatBotFm.requireActivity(),
//                    mItem.get("user_image")?.run { if (this is JsonNull) "" else asString },
//                    lBinding.profileIv)
//                lBinding.unreadTv.text = mItem.get("chat_content").asString

                //날짜 표시기 처리 - 3가지 깐깐한 조건이 충족되지 않으면 표시되지않으니 안심!
//                if (mItem.get("is_dayChanged") != null && !mItem.get("is_dayChanged").isJsonNull && mItem.get("is_dayChanged").asInt == 1) {
//                    lBinding.dateLayout.visibility = View.VISIBLE
//                    val chatDate = LocalDateTime.parse(mItem.get("create_date").asString, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"))
//                    val uiDate = chatDate.format(DateTimeFormatter.ofPattern("MM월 dd일 yyyy년"))
//                    lBinding.dateDelimiter.text = uiDate
//                } else {
//                    lBinding.dateLayout.visibility = View.GONE
//                }

//                lBinding.chatWriter.text = ""
                lBinding.chatContent.text = mItem.asString
//                lBinding.chatDate.text = mItem.asString



            //나의 뷰홀더일때
            } else if(mBinding is ChatbotFmVhBinding){
                rBinding = mBinding as ChatbotFmVhBinding
                if(absoluteAdapterPosition < 6){
                    rBinding.constraintLayout3.visibility = View.GONE
                }

//                rBinding.unreadTv.text = mItem.get("chat_content").asString


                //날짜 표시기 처리 - 3가지 깐깐한 조건이 충족되지 않으면 표시되지않으니 안심!
//                if (mItem.get("is_dayChanged") != null && !mItem.get("is_dayChanged").isJsonNull && mItem.get("is_dayChanged").asInt == 1) {
//                    rBinding.dateLayout.visibility = View.VISIBLE
//                    val chatDate = LocalDateTime.parse(mItem.get("create_date").asString, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"))
//                    val uiDate = chatDate.format(DateTimeFormatter.ofPattern("MM월 dd일 yyyy년"))
//                    rBinding.dateDelimiter.text = uiDate
//                } else {
//                    rBinding.dateLayout.visibility = View.GONE
//                }



                rBinding.chatContent.text = mItem.asString
//                rBinding.chatContent.text = mItem.get("chat_content").asString
//                rBinding.chatDate.text = mItem.get("create_date").asString




            }



        }


    }
}