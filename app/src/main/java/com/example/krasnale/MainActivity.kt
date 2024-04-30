package com.example.krasnale

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private  var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (selectedImageUri == null)
        {
            findViewById<ImageView>(R.id.imgView).setImageResource(R.drawable.ic_launcher_foreground)
        }


        findViewById<ImageView>(R.id.imgView).setOnClickListener{
            openImgChooser()
        }

        findViewById<Button>(R.id.btn_Upload).setOnClickListener{
            uploadImage()
        }
    }

    private fun openImgChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE)
        }
    }

    private fun uploadImage() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Activity.RESULT_OK){
            when(requestCode)
            {
                REQUEST_CODE_IMAGE -> {
                    selectedImageUri = data?.data
                    val ImV = findViewById<ImageView>(R.id.imgView)
                    ImV.setImageURI(selectedImageUri)
                }
            }
        }
    }

    companion object{
        const val REQUEST_CODE_IMAGE = 101
    }
}