package com.rakesh.photoeditor.Screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.photoeditor.Adapters.ImageListAdpter
import com.rakesh.photoeditor.MainActivity
import com.rakesh.photoeditor.R
import io.paperdb.Paper


class ImageListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_imagelist, container, false)

        val rv_imageList=view.findViewById<RecyclerView>(R.id.rv_imageList)
        val cl_noimage_list=view.findViewById<ConstraintLayout>(R.id.cl_noimage_list)

        setupRecyclerView(rv_imageList,cl_noimage_list)

        return view
    }

    private fun setupRecyclerView(rv_imageList: RecyclerView, cl_noimage_list: ConstraintLayout) {

        if (Paper.book().contains("SavedImages")){
            //collecteing the Stored Images Path
            val arrrayofImages=Paper.book().read("SavedImages") as ArrayList<String>
            //seting up Recycler View
            val ImageListAdpter= ImageListAdpter(MainActivity.mMainActivity,arrrayofImages)
            rv_imageList.adapter=ImageListAdpter

            //No Images In the List
            cl_noimage_list.visibility=View.GONE

        }else{
            cl_noimage_list.visibility=View.VISIBLE
        }

    }


    override fun onResume() {
        super.onResume()
        MainActivity.fab_add_pics.visibility=View.VISIBLE
        MainActivity.toolbar_main.visibility=View.VISIBLE
    }




}