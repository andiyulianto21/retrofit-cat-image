package com.example.cobaretrofitapi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cobaretrofitapi.databinding.ActivityEditUserBinding
import com.example.cobaretrofitapi.pojo.ResponseChanged
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private var id: Int = 0
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var imageUrl: String
    private lateinit var path: String
    private var isImageChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Edit User"
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getIntExtra(ResultActivity.EXTRA_ID, 0)
        name = intent.getStringExtra(ResultActivity.EXTRA_NAME).toString()
        email = intent.getStringExtra(ResultActivity.EXTRA_EMAIL).toString()
        imageUrl = intent.getStringExtra(ResultActivity.EXTRA_IMAGE).toString()

        binding.inputEmailEdit.setText(email)
        binding.inputNameEdit.setText(name)
        binding.tvImagePath.text = FileUtils.getPath(this, Uri.parse(imageUrl))
        Picasso.get().load(imageUrl).into(binding.imgUploadEdit)

        binding.btnChange.setOnClickListener { 
            val intentCamera = Intent(Intent.ACTION_GET_CONTENT)
            intentCamera.type = "image/*"
            getContentFromCamera.launch(Intent.createChooser(intentCamera, "Select replacement image"))
        }
        
        binding.btnEdit.setOnClickListener {
            editUser()
        }
    }
    
    private val getContentFromCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val uri = result.data?.data
            path = FileUtils.getPath(this, uri!!)
            isImageChanged = true
            Picasso.get().load(uri).into(binding.imgUploadEdit)
            binding.tvImagePath.text = path
            Toast.makeText(this, "Image changed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editUser() {
        if(binding.inputNameEdit.text.toString().isEmpty()){
            binding.inputNameEdit.error = "Empty"
        }else if(binding.inputEmailEdit.text.toString().isEmpty()) {
            binding.inputEmailEdit.error = "Empty"
        }else {
            editApiService()
        }
    }

    private fun editApiService() {
        val id = id.toString()
        val reqId = RequestBody.create("text/plain".toMediaTypeOrNull(),id)
        val reqName = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.inputNameEdit.text.toString())
        val reqEmail = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.inputEmailEdit.text.toString())

        if(!isImageChanged){
            updateWithoutImage(reqId, reqEmail, reqName)
//            Toast.makeText(this, "without image", Toast.LENGTH_SHORT).show()
        }else {
            updateWithImage(reqId, reqEmail, reqName)
//            Toast.makeText(this, "with image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateWithoutImage(
        reqId: RequestBody,
        reqEmail: RequestBody,
        reqName: RequestBody
    ) {
        val call = RetrofitBuilder.apiService().updateUser(id = reqId, name = reqName, email = reqEmail)
        call.enqueue(object: Callback<ResponseChanged> {
            override fun onResponse(call: Call<ResponseChanged>, response: Response<ResponseChanged>) {
                if(!response.isSuccessful){
                    Toast.makeText(this@EditUserActivity, response.code(), Toast.LENGTH_SHORT).show()
                }
                val body = response.body()!!
                if(body.status){
                    AlertDialog.Builder(this@EditUserActivity)
                        .setTitle("Edit")
                        .setIcon(R.drawable.ic_true)
                        .setMessage(body.message)
                        .setPositiveButton("OK"){dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent(this@EditUserActivity, ResultActivity::class.java))
                            finish()
                        }.show()
                }else {
                    AlertDialog.Builder(this@EditUserActivity)
                        .setTitle("Edit")
                        .setIcon(R.drawable.ic_false)
                        .setMessage(body.message)
                        .setPositiveButton("OK"){dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
            }

            override fun onFailure(call: Call<ResponseChanged>, t: Throwable) {
                Toast.makeText(this@EditUserActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateWithImage(
        reqId: RequestBody,
        reqEmail: RequestBody,
        reqName: RequestBody
    ) {
        val path = binding.tvImagePath.text.toString()
        val file = File(path)
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val multipart = MultipartBody.Part.createFormData("imageEdit", file.name, requestBody)

        val call = RetrofitBuilder.apiService().updateUser(id = reqId, name = reqName, email = reqEmail, imageEdit = multipart)
        call.enqueue(object: Callback<ResponseChanged> {
            override fun onResponse(call: Call<ResponseChanged>, response: Response<ResponseChanged>) {
                if(!response.isSuccessful){
                    Toast.makeText(this@EditUserActivity, response.code(), Toast.LENGTH_SHORT).show()
                }
                val body = response.body()!!
                if(body.status){
                    AlertDialog.Builder(this@EditUserActivity)
                        .setTitle("Edit")
                        .setIcon(R.drawable.ic_true)
                        .setMessage(response.message())
                        .setPositiveButton("OK"){dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }else {
                    AlertDialog.Builder(this@EditUserActivity)
                        .setTitle("Edit")
                        .setIcon(R.drawable.ic_false)
                        .setMessage(response.message())
                        .setPositiveButton("OK"){dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
            }

            override fun onFailure(call: Call<ResponseChanged>, t: Throwable) {
                Toast.makeText(this@EditUserActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}