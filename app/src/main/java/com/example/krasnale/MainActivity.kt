package com.example.krasnale

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {
    private  var selectedImageUri: Uri? = null

    private val CAMERA_PERMISSION_CODE = 1000
    private val READ_PERMISSION_CODE = 1001

    private val IMAGE_CHOOSE = 1000
    private val IMAGE_CAPTURE = 1001

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
            val permissionGranted = requestStoragePermission()

            if (permissionGranted){
                openImgChooser()
            }
        }

        findViewById<Button>(R.id.btn_Upload).setOnClickListener{
            uploadImage()
        }

        findViewById<Button>(R.id.btn_TakePhoto).setOnClickListener {
            val permissionGranted = requestCameraPermission()
            if (permissionGranted){
                takePhoto()
            }
        }
    }

    private fun requestStoragePermission(): Boolean{
        var permissionGranted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val storagePermissionNotGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
            if (storagePermissionNotGranted) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, READ_PERMISSION_CODE)
            } else {
                permissionGranted = true
            }

        } else {
            permissionGranted = true
        }
        return permissionGranted
    }

    private  fun requestCameraPermission(): Boolean{
        var permissionGranted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionsNotGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionsNotGranted){
                val permission = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permission, CAMERA_PERMISSION_CODE)
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
        return permissionGranted
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //openCameraInterface()
                takePhoto()
            } else {
                //showAlert("Camera permission denied.")
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == READ_PERMISSION_CODE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val storagePermissionGranted = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                if (storagePermissionGranted){
                    openImgChooser()
                } else {
                    //showAlert("Storage permission denied.")
                    Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show()
                }
            } else {
                //showAlert("Storage permission not needed.")
                Toast.makeText(this, "Storage permission not needed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun openImgChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    private fun uploadImage() {
        if (selectedImageUri == null)
        {
            Toast.makeText(this,"Select image first!",Toast.LENGTH_SHORT).show()
            return
        }

        val parcelFileDescriptor = contentResolver.openFileDescriptor(
            selectedImageUri!!,"r",null
        )?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFilename(selectedImageUri!!))
        Toast.makeText(this,"File selected",Toast.LENGTH_SHORT).show()

        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val progbar = findViewById<ProgressBar>(R.id.progBar)
        progbar.progress = 0
        val body = UploadRequestBody(file,"image",this)
        Toast.makeText(this,"UploadBodyRequested",Toast.LENGTH_SHORT).show()


        MyApi().uploadImage(MultipartBody.Part.createFormData(
            "image",
            file.name,
            body
        ),
            RequestBody.create(MediaType.parse("multipatrt/form-data"),"json")
        ).enqueue(object : Callback<UploaadResponse>{
            override fun onResponse(call: Call<UploaadResponse>, response: Response<UploaadResponse>) {
               response.body()?.let {
                   Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
                   //Toast.makeText(this@MainActivity,"onResponse",Toast.LENGTH_SHORT).show()
                   progbar.progress = 100
               }
            }

            override fun onFailure(p0: Call<UploaadResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message,Toast.LENGTH_LONG).show()
                //Toast.makeText(this@MainActivity, "Faliure",Toast.LENGTH_SHORT).show()
                progbar.progress = 0            }

        })
    }

    private fun takePhoto(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, R.string.take_picture)
        selectedImageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //create camera intent
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)

        //launch intent
        startActivityForResult(intent, IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ImV = findViewById<ImageView>(R.id.imgView)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE){
            ImV.setImageURI(selectedImageUri)
        } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CHOOSE){
            selectedImageUri = data?.data
            //ImV.setImageURI(data?.data)
            ImV.setImageURI(selectedImageUri)
        }
    }

    override fun onProgresUpdate(percentage: Int) {
        findViewById<ProgressBar>(R.id.progBar).progress = percentage
    }
}

private fun ContentResolver.getFilename(selectedImageUri: Uri): String {
    var name = ""
    val returnCursor = this.query(selectedImageUri,null,null,null,null)

    if (returnCursor != null){
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}
