package com.rakesh.photoeditor

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.paperdb.Paper
import java.io.ByteArrayOutputStream





class MainActivity : AppCompatActivity() {

    private val READ_STORAGE_REQUEST_CODE=123

    companion object{
        //for the Global Access
        lateinit var fab_add_pics: FloatingActionButton
        lateinit var toolbar_main: Toolbar
        lateinit var mMainActivity: MainActivity
        lateinit var navController: NavController
        val FileName="neoastraSavedImage"
         val IMAGE_DIRECTORY = "/neoastra/SaveImage"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        //intialize the views
        initview()

        //for store local small amount of data
        Paper.init(this)


    }


    private fun initview() {
        //for the Global Access
        toolbar_main=findViewById(R.id.toolbar_main)
        fab_add_pics=findViewById(R.id.fab_add_pics)
        mMainActivity=this

        //Floateing Button intialize and Click Listner
        findViewById<FloatingActionButton>(R.id.fab_add_pics).setOnClickListener {
            setupPermissions()
        }


    }


    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionWrite = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        //if the permission is Denied
        if (permission != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the Read Storage is required for this app to Read Images.")
                        .setTitle("Permission required")
                            builder.setPositiveButton(
                                "OK"
                            ) { dialog, id ->

                        makeRequest()
                    }

                    val dialog = builder.create()
                dialog.show()
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ){

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the Write Storage is required for this app to Write Images.")
                    .setTitle("Permission required")
                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->

                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()

            }else{
                makeRequest()
            }
        }else{
            //if the Permission is already Grant open galley to access pics
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, READ_STORAGE_REQUEST_CODE)
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            READ_STORAGE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {


        if (resultCode == RESULT_OK && requestCode == READ_STORAGE_REQUEST_CODE) {
            try {
                val inputStream=contentResolver.openInputStream(data!!.data!!)
                val bitmap=BitmapFactory.decodeStream(inputStream)

                if (bitmap!= null){
                    val baos = ByteArrayOutputStream()
                    //
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos)

                    val bundle = bundleOf("imageSelected" to bitmap)
                    navController.navigate(R.id.imageEditFragment, bundle)
                }
            }catch (e: Exception){

            }
        }
        }catch (e: Exception){

        }

    }


}