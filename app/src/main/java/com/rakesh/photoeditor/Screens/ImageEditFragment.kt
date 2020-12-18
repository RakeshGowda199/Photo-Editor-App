package com.rakesh.photoeditor.Screens

import android.content.DialogInterface
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

    var SeletedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mImageSelectedBitmap = requireArguments().get("imageSelected") as Bitmap
        Log.i("Image_Seletced", mImageSelectedBitmap.toString())
        fab_add_pics.visibility=View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root_View = inflater.inflate(R.layout.fragment_image_edit, container, false)

        //intalize the View's
        initView(root_View)

        return root_View
    }

    private fun initView(view: View) {

        //intialize the Required View
        val imageFilterView_selected = view.findViewById<ImageFilterView>(R.id.imageFilterView_selected)
        val iv_effects = view.findViewById<ImageView>(R.id.iv_effects)
        val iv_rotate_clk = view.findViewById<ImageView>(R.id.iv_rotate_clk)
        val iv_rortate_anticlk = view.findViewById<ImageView>(R.id.iv_rortate_anticlk)
        val iv_crop_image = view.findViewById<ImageView>(R.id.iv_crop_image)
        val sb_cropimage = view.findViewById<SeekBar>(R.id.sb_cropimage_horizontal)
        val seekBar_vertical = view.findViewById<SeekBar>(R.id.seekBar_vertical)
        val btn_save = view.findViewById<Button>(R.id.btn_save)
        val tv_y_axis = view.findViewById<TextView>(R.id.tv_y_axis)
        val tv_x_axis = view.findViewById<TextView>(R.id.tv_x_axis)

        sb_cropimage.max = mImageSelectedBitmap.height-20
        seekBar_vertical.max =  mImageSelectedBitmap.width-20

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sb_cropimage.min = 0
            seekBar_vertical.min = 0
        }
        //Seek bar Crop Horizontal
        sb_cropimage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val croppedBmp: Bitmap =
                    Bitmap.createBitmap(
                        mImageSelectedBitmap,
                        0,
                        p1,
                        mImageSelectedBitmap.getWidth(),
                        mImageSelectedBitmap.getHeight() - p1
                    )
                imageFilterView_selected.setImageBitmap(croppedBmp)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        //Seek bar Crop Vertical
        seekBar_vertical.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {


                val croppedBmp: Bitmap =
                    Bitmap.createBitmap(
                        mImageSelectedBitmap,
                        p1,
                        0,
                        mImageSelectedBitmap.getWidth() - p1,
                        mImageSelectedBitmap.getHeight()
                    )
                imageFilterView_selected.setImageBitmap(croppedBmp)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        Glide.with(mMainActivity)
            .load(mImageSelectedBitmap)
            .into(imageFilterView_selected)


        //rotate Anti-Clock Wise
        var isAlradyClicked = false
        var mRotateImage = 90.0F
        iv_rotate_clk.setOnClickListener {
            if (isAlradyClicked) {
                if (mRotateImage == 360F) {
                    mRotateImage = 0F
                } else {
                    mRotateImage = mRotateImage + 90.0F
                }
            } else {
                mRotateImage = 90.0F
            }
            isAlradyClicked = true
            imageFilterView_selected.rotation = mRotateImage
        }

        //rotate Clock Wise
        iv_rortate_anticlk.setOnClickListener {
            if (isAlradyClicked) {
                if (mRotateImage == 0F) {
                    mRotateImage = 360F
                } else {
                    mRotateImage = mRotateImage - 90.0F

                }
            } else {
                mRotateImage = 90.0F

            }
            isAlradyClicked = true
            imageFilterView_selected.rotation = mRotateImage
        }

        //effects
        iv_effects.setOnClickListener {
            mEffectsToImage(imageFilterView_selected)
        }

        //Crop Image
        var isCropalready=false
        iv_crop_image.setOnClickListener {

            if (isCropalready){
                isCropalready=false
                sb_cropimage.visibility=View.INVISIBLE
                seekBar_vertical.visibility=View.INVISIBLE
                tv_y_axis.visibility=View.INVISIBLE
                tv_x_axis.visibility=View.INVISIBLE
            }else{
                isCropalready=true
                sb_cropimage.visibility=View.VISIBLE
                seekBar_vertical.visibility=View.VISIBLE
                tv_y_axis.visibility=View.VISIBLE
                tv_x_axis.visibility=View.VISIBLE
            }

        }


        btn_save.setOnClickListener {
            val imageBitmap=imageFilterView_selected.drawable.toBitmap()

            val str_path=saveImage(imageBitmap)

            _HandleLocalStore(str_path.toString())
        }
    }

    private fun _HandleLocalStore(imageToStore: String) {
        var arrayofSavedImages=ArrayList<String>()
        if (Paper.book().contains("SavedImages")){
            arrayofSavedImages=Paper.book().read("SavedImages")
            arrayofSavedImages.add(imageToStore)
            Paper.book().write("SavedImages", arrayofSavedImages)
        }else{
            arrayofSavedImages.add(imageToStore)
            Paper.book().write("SavedImages", arrayofSavedImages)
        }

        val printData=Paper.book().read("SavedImages") as ArrayList<String>
        Log.d("SavedImagepath",imageToStore)
        Log.d("SavedImage",printData.toString())
        mSuccessAlertMessage()
    }

    private fun mSuccessAlertMessage() {
        MaterialAlertDialogBuilder(mMainActivity)
            .setTitle("Sucess!!!")
            .setMessage("Saved Sucessfully !!!")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                MainActivity.navController.navigate(R.id.ImageListFragment)
            })
            .show()
    }

    private fun mEffectsToImage(imageFilterView: ImageFilterView) {


        val effects = arrayOf("Normal", "contrast", "saturation", "warmth", "crossfade")
        var SelectedEffect = effects[SeletedIndex]
        MaterialAlertDialogBuilder(mMainActivity)
            .setTitle("Select The Effect")
            .setSingleChoiceItems(effects, SeletedIndex) { dialog, which ->
                SeletedIndex = which
                SelectedEffect = effects[SeletedIndex]
            }
            .setPositiveButton("Submit", DialogInterface.OnClickListener { dialogInterface, i ->
                when (SeletedIndex) {
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
            })
            .show()
    }


    fun saveImage(myBitmap: Bitmap): String? {
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
            MediaScannerConnection.scanFile(mMainActivity, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
            fo.close()
            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }




}