package com.example.githubuser.data.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.UI.ListUserActivity
import com.example.githubuser.R
import com.example.githubuser.UI.UserDetails.DetailUserActivity
import com.example.githubuser.UI.UserDetails.DetailUserFragmentFollower
import com.example.githubuser.data.entity.User


class UserAdapter(var listUser: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ListViewHolder>() {

    companion object {
        val USER_KEY = "username"
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_user, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar_url)
            .apply(RequestOptions().override(55, 55))
            .into(holder.imgPhoto)
        holder.tvName.text = user.login
        holder.tvHtml.text = user.html_url


        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(USER_KEY, user.login)
            val myFrag = DetailUserFragmentFollower()
            myFrag.setArguments(bundle)

            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
            intent.putExtra(USER_KEY, user.login)
            intent.putExtra(ListUserActivity.EXTRA_USER, user)
            holder.itemView.context.startActivity(intent)

            // Creating the new Fragment with the name passed in.
        }
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_username)
        var tvHtml: TextView = itemView.findViewById(R.id.tv_html)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }
}