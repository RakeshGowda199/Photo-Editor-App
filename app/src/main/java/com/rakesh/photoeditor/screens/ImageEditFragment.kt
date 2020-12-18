package com.rakesh.photoeditor.screens

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rakesh.photoeditor.MainActivity
import com.rakesh.photoeditor.MainActivity.Companion.FileName
import com.rakesh.photoeditor.MainActivity.Companion.IMAGE_DIRECTORY
import com.rakesh.photoeditor.MainActivity.Companion.fab_add_pics
import com.rakesh.photoeditor.MainActivity.Companion.mMainActivity
import com.rakesh.photoeditor.R
import io.paperdb.Paper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ImageEditFragment : Fragment() {

    lateinit var mImageSelectedBitmap: Bitmap

    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mImageSelectedBitmap = requireArguments().get("imageSelected") as Bitmap
        Log.i("Image_Seletced", mImageSelectedBitmap.toString())
        fab_add_pics.visibility = View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_image_edit, container, false)

        //initialize the View's
        initView(rootView)

        return rootView
    }

    private fun initView(view: View) {

        //initialize the Required View
        val imageFilterViewSelected =
            view.findViewById<ImageFilterView>(R.id.imageFilterView_selected)
        val ivEffects = view.findViewById<ImageView>(R.id.iv_effects)
        val ivRotateClk = view.findViewById<ImageView>(R.id.iv_rotate_clk)
        val ivRotateAnticlk = view.findViewById<ImageView>(R.id.iv_rortate_anticlk)
        val ivCropImage = view.findViewById<ImageView>(R.id.iv_crop_image)
        val sbCropImage = view.findViewById<SeekBar>(R.id.sb_cropimage_horizontal)
        val seekBarVertical = view.findViewById<SeekBar>(R.id.seekBar_vertical)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val tvYAxis = view.findViewById<TextView>(R.id.tv_y_axis)
        val tvXAxis = view.findViewById<TextView>(R.id.tv_x_axis)

        sbCropImage.max = mImageSelectedBitmap.height - 20
        seekBarVertical.max = mImageSelectedBitmap.width - 20

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sbCropImage.min = 0
            seekBarVertical.min = 0
        }
        //Seek bar Crop Horizontal
        sbCropImage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val croppedBmp: Bitmap =
                    Bitmap.createBitmap(
                        mImageSelectedBitmap,
                        0,
                        p1,
                        mImageSelectedBitmap.width,
                        mImageSelectedBitmap.height - p1
                    )
                imageFilterViewSelected.setImageBitmap(croppedBmp)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        //Seek bar Crop Vertical
        seekBarVertical.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {


                val croppedBmp: Bitmap =
                    Bitmap.createBitmap(
                        mImageSelectedBitmap,
                        p1,
                        0,
                        mImageSelectedBitmap.width - p1,
                        mImageSelectedBitmap.height
                    )
                imageFilterViewSelected.setImageBitmap(croppedBmp)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        Glide.with(mMainActivity)
            .load(mImageSelectedBitmap)
            .into(imageFilterViewSelected)


        //rotate Anti-Clock Wise
        var isAlradyClicked = false
        var mRotateImage = 90.0F
        ivRotateClk.setOnClickListener {
            mRotateImage = if (isAlradyClicked) {
                if (mRotateImage == 360F) {
                    0F
                } else {
                    mRotateImage + 90.0F
                }
            } else {
                90.0F
            }
            isAlradyClicked = true
            imageFilterViewSelected.rotation = mRotateImage
        }

        //rotate Clock Wise
        ivRotateAnticlk.setOnClickListener {
            mRotateImage = if (isAlradyClicked) {
                if (mRotateImage == 0F) {
                    360F
                } else {
                    mRotateImage - 90.0F

                }
            } else {
                90.0F

            }
            isAlradyClicked = true
            imageFilterViewSelected.rotation = mRotateImage
        }

        //effects
        ivEffects.setOnClickListener {
            mEffectsToImage(imageFilterViewSelected)
        }

        //Crop Image
        var isCropalready = false
        ivCropImage.setOnClickListener {

            if (isCropalready) {
                isCropalready = false
                sbCropImage.visibility = View.INVISIBLE
                seekBarVertical.visibility = View.INVISIBLE
                tvYAxis.visibility = View.INVISIBLE
                tvXAxis.visibility = View.INVISIBLE
            } else {
                isCropalready = true
                sbCropImage.visibility = View.VISIBLE
                seekBarVertical.visibility = View.VISIBLE
                tvYAxis.visibility = View.VISIBLE
                tvXAxis.visibility = View.VISIBLE
            }

        }


        btnSave.setOnClickListener {
            val imageBitmap = imageFilterViewSelected.drawable.toBitmap()

            val strPath = saveImage(imageBitmap)

            handleLocalStore(strPath.toString())
        }
    }

    private fun handleLocalStore(imageToStore: String) {
        var arrayofSavedImages = ArrayList<String>()
        if (Paper.book().contains("SavedImages")) {
            arrayofSavedImages = Paper.book().read("SavedImages")
            arrayofSavedImages.add(imageToStore)
            Paper.book().write("SavedImages", arrayofSavedImages)
        } else {
            arrayofSavedImages.add(imageToStore)
            Paper.book().write("SavedImages", arrayofSavedImages)
        }

        val printData = Paper.book().read("SavedImages") as ArrayList<String>
        Log.d("SavedImagepath", imageToStore)
        Log.d("SavedImage", printData.toString())
        mSuccessAlertMessage()
    }

    private fun mSuccessAlertMessage() {
        MaterialAlertDialogBuilder(mMainActivity)
            .setTitle("Sucess!!!")
            .setMessage("Saved Sucessfully !!!")
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                MainActivity.navController.navigate(R.id.ImageListFragment)
            }
            .show()
    }

    private fun mEffectsToImage(imageFilterView: ImageFilterView) {


        val effects = arrayOf("Normal", "contrast", "saturation", "warmth", "crossfade")
        var _SelectedEffect = effects[selectedIndex]
        MaterialAlertDialogBuilder(mMainActivity)
            .setTitle("Select The Effect")
            .setSingleChoiceItems(effects, selectedIndex) { _, which ->
                selectedIndex = which
                _SelectedEffect = effects[selectedIndex]
            }
            .setPositiveButton("Submit") { dialogInterface, _ ->
                when (selectedIndex) {
                    0 -> {
                        imageFilterView.contrast = 1F
                        imageFilterView.saturation = 1F
                        imageFilterView.warmth = 1F
                        imageFilterView.crossfade = 1F
                    }
                    1 -> {
                        imageFilterView.contrast = 0.5F
                        imageFilterView.saturation = 1F
                        imageFilterView.warmth = 1F
                        imageFilterView.crossfade = 1F

                    }
                    2 -> {
                        imageFilterView.saturation = 0.5F
                        imageFilterView.contrast = 1F
                        imageFilterView.warmth = 1F
                        imageFilterView.crossfade = 1F

                    }
                    3 -> {
                        imageFilterView.warmth = 0.5F
                        imageFilterView.saturation = 1F
                        imageFilterView.contrast = 1F
                        imageFilterView.crossfade = 1F

                    }
                    4 -> {
                        imageFilterView.crossfade = 0.5F
                        imageFilterView.saturation = 1F
                        imageFilterView.warmth = 1F
                        imageFilterView.contrast = 1F
                    }
                }
                dialogInterface.dismiss()
            }
            .show()
    }


    private fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val imageDirectory = Environment.getExternalStoragePublicDirectory(IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs()
        }
        try {

            val f = File(
                imageDirectory,
                FileName + "_" + Calendar.getInstance().timeInMillis.toString() + ".png"
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                mMainActivity,
                arrayOf(f.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }


}