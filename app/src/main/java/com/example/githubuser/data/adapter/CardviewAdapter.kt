package com.example.githubuser.data.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.UI.ListUserActivity
import com.example.githubuser.R
import com.example.githubuser.UI.UserDetails.DetailUserActivity
import com.example.githubuser.data.entity.User
import kotlinx.android.synthetic.main.item_note.view.*

class CardviewAdapter(private val activity: Activity) : RecyclerView.Adapter<CardviewAdapter.UserViewHolder>() {
    var listUser = ArrayList<User>()
        set(listUser) {
            if (listUser.size > 0) {
                this.listUser.clear()
            }
            this.listUser.addAll(listUser)

            notifyDataSetChanged()
        }

    fun addItem(note: User) {
        this.listUser.add(note)
        notifyItemInserted(this.listUser.size - 1)
    }

    fun updateItem(position: Int, note: User) {
        this.listUser[position] = note
        notifyItemChanged(position, note)
    }

    fun removeItem(position: Int) {
        this.listUser.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listUser.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = this.listUser.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            with(itemView){
                tv_fav_username.text = user.login
                tv_fav_html.text = user.html_url

                Glide.with(itemView.context)
                    .load(user.avatar_url)
                    .apply(RequestOptions().override(55, 55))
                    .into(itemView.fav_img)

                cv_item_note.setOnClickListener{
                    val intent = Intent(itemView.context, DetailUserActivity::class.java)
                    intent.putExtra(UserAdapter.USER_KEY, user.login)
                    intent.putExtra(ListUserActivity.EXTRA_USER, user)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
