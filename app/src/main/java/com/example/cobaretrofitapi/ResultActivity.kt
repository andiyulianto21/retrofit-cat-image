package com.example.cobaretrofitapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobaretrofitapi.adapter.RvAdapterUsers
import com.example.cobaretrofitapi.databinding.ActivityResultBinding
import com.example.cobaretrofitapi.pojo.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultActivity : AppCompatActivity(), RvAdapterUsers.OnItemClickedListener {

    companion object {
        const val EXTRA_ID = "extraId"
        const val EXTRA_NAME = "extraName"
        const val EXTRA_EMAIL = "extraEmail"
        const val EXTRA_IMAGE = "extraImage"
    }

    private lateinit var binding: ActivityResultBinding
    private lateinit var userList: List<Users>
    private lateinit var call: Call<List<Users>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "List of Users"
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showListUser()
    }

    private fun showListUser() {
        binding.rvImageUser.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        call = RetrofitBuilder.apiService().getUserImage()
        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    userList = response.body()!!
                    val adapter = RvAdapterUsers(userList, this@ResultActivity)
                    binding.rvImageUser.adapter = adapter
                } else {
                    Toast.makeText(this@ResultActivity, "gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                Toast.makeText(this@ResultActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onItemClicked(users: Users) {
        val intent = Intent(this, SelectedUserActivity::class.java)
        intent.putExtra(EXTRA_ID, users.id)
        intent.putExtra(EXTRA_NAME, users.name)
        intent.putExtra(EXTRA_EMAIL, users.email)
        intent.putExtra(EXTRA_IMAGE, users.image_url)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        showListUser()
    }
}