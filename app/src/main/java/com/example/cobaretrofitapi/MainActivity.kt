package com.example.cobaretrofitapi

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cobaretrofitapi.databinding.ActivityMainBinding
import com.example.cobaretrofitapi.pojo.ResponseUpload
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var path: String
    private lateinit var fileName: String

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_menu -> startActivity(Intent(this, ResultActivity::class.java))
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Create New User"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUpload.setOnClickListener {
            if(isStoragePermissionGranted()){
                uploadGranted()
            }
        }

        binding.btnSend.setOnClickListener {
            sendDataToDB()
        }
    }

    private fun sendDataToDB() {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        if(name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Input Form empty", Toast.LENGTH_SHORT).show()
        }else {
            val requestName = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val requestEmail = RequestBody.create("text/plain".toMediaTypeOrNull(), email)

            val file: File = File(path)
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val imageMultiBody = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val call = RetrofitBuilder.apiService().uploadImage(name = requestName, email = requestEmail, image = imageMultiBody)

            call.enqueue(object : Callback<ResponseUpload>{
                override fun onResponse(
                    call: Call<ResponseUpload>,
                    response: Response<ResponseUpload>
                ) {
                    if(!response.isSuccessful){
                        Toast.makeText(this@MainActivity, response.code(), Toast.LENGTH_SHORT).show()
                    }
                    val result = response.body()!!
                    if(!result.status) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Berhasil")
                            .setIcon(R.drawable.ic_false)
                            .setMessage(result.message)
                            .setPositiveButton("OK"
                            ) { dialog, _ -> dialog.dismiss() }.show()
                    }else {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Berhasil")
                            .setIcon(R.drawable.ic_true)
                            .setMessage(result.message)
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                            }.show()
                    }
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                true
            } else {
                Toast.makeText(this, "Permission is revoked", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            uploadGranted()
        }
    }

    private fun uploadGranted() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        openGallery.launch(Intent.createChooser(intent, "Select image to upload"))
    }

    private val openGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if(result.resultCode == Activity.RESULT_OK){
            Toast.makeText(this, "masuk ke result ok", Toast.LENGTH_SHORT).show()
            val uri = result.data?.data
            val path = FileUtils.getPath(this, uri!!)
            Picasso.get().load(uri).into(binding.imgUpload)
            this.path = path
            this.fileName = FileUtils.getFileName(this, uri)!!
            binding.btnUpload.text = path
        }else{
            Toast.makeText(this, "result is not ok", Toast.LENGTH_SHORT).show()
        }
    }

}