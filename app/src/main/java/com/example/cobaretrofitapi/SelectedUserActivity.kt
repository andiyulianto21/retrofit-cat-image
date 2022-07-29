package com.example.cobaretrofitapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.cobaretrofitapi.databinding.ActivitySelectedUserBinding
import com.squareup.picasso.Picasso

class SelectedUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedUserBinding
    private var id: Int = 0
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Selected User"
        binding = ActivitySelectedUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getIntExtra(ResultActivity.EXTRA_ID, 0)
        name = intent.getStringExtra(ResultActivity.EXTRA_NAME).toString()
        email = intent.getStringExtra(ResultActivity.EXTRA_EMAIL).toString()
        imageUrl = intent.getStringExtra(ResultActivity.EXTRA_IMAGE).toString()

        binding.tvEmailSelected.text = email
        binding.tvNameSelected.text = name
        Picasso.get().load(imageUrl).into(binding.imageUserSelected)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.selected_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_edit -> {
                val intent =Intent(this@SelectedUserActivity, EditUserActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_NAME, name)
                intent.putExtra(ResultActivity.EXTRA_ID, id)
                intent.putExtra(ResultActivity.EXTRA_EMAIL, email)
                intent.putExtra(ResultActivity.EXTRA_IMAGE, imageUrl)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}