package com.example.cobaretrofitapi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cobaretrofitapi.databinding.CardUserBinding
import com.example.cobaretrofitapi.pojo.Users
import com.squareup.picasso.Picasso

class RvAdapterUsers(private val userList: List<Users>, private val onItemClickedListener: OnItemClickedListener) : RecyclerView.Adapter<RvAdapterUsers.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(val binding: CardUserBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener { onItemClickedListener.onItemClicked(userList[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding: CardUserBinding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            Picasso.get().load(userList[position].image_url).into(this.imageUser)
            this.tvName.text = userList[position].name
            this.tvEmail.text = userList[position].email
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface OnItemClickedListener{
        fun onItemClicked(users: Users)
    }

}