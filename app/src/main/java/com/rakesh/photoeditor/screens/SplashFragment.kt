package com.rakesh.photoeditor.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.rakesh.photoeditor.MainActivity.Companion.fab_add_pics
import com.rakesh.photoeditor.MainActivity.Companion.mMainActivity
import com.rakesh.photoeditor.MainActivity.Companion.navController
import com.rakesh.photoeditor.MainActivity.Companion.toolbar_main
import com.rakesh.photoeditor.R


class SplashFragment : Fragment() {
    // Splash screen timer
    private val SPLASH_SCREEN_TIME_OUT = 5000L

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val Spalsh_View = inflater.inflate(R.layout.fragment_splash, container, false)

        //if image is from api or Server Call we can use this
        mImageViewDesign(Spalsh_View.findViewById(R.id.img_splash))

        //to handle splash Timer
        mSplashTimerScreen()

        return Spalsh_View
    }

    private fun mImageViewDesign(imageView: ImageView) {

        Glide.with(mMainActivity)
            .load(R.drawable.image_editior_logo)
            .transform(RoundedCorners(80))
            .into(imageView)

    }

    //to handle the hide and show for toolbar and float button
    override fun onResume() {
        super.onResume()
        fab_add_pics.visibility=View.GONE
        toolbar_main.visibility=View.GONE
    }



    private fun mSplashTimerScreen() {

        //Global Access
        navController=findNavController()

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.ImageListFragment)
        }, SPLASH_SCREEN_TIME_OUT)
    }
}