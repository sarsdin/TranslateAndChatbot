package com.tappcli.tr

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tappcli.MainActivity
import com.tappcli.databinding.FavoriteFmBinding

class FavoriteFm : Fragment() {
    val tagName = "[FavoriteFm]"
    lateinit var homeVm: HomeVm

    lateinit var rv: RecyclerView
//    lateinit var rva: FavoriteFmRva


    var mbinding: FavoriteFmBinding? = null
    val binding get() = mbinding!! //null체크를 매번 안하게끔 재 선언




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeVm = ViewModelProvider(requireActivity()).get(HomeVm::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mbinding = FavoriteFmBinding.inflate(inflater, container, false)


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        리사이클러뷰설정()


        //뒤로가기
        binding.backIb.setOnClickListener {
            findNavController().navigateUp()
        }



    }

    private fun 리사이클러뷰설정() {
        //어댑터 세팅
        val lm = LinearLayoutManager(binding.root.context)
        lm.orientation = LinearLayoutManager.VERTICAL
        rv = binding.listView
        rv.setLayoutManager(lm)
        rv.setAdapter(FavoriteFmRva(homeVm, requireActivity() as MainActivity))
    }


    override fun onResume() {
        super.onResume()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }

}