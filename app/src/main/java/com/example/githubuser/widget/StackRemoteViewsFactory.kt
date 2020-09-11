package com.example.githubuser.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.example.githubuser.UI.ListUserActivity.Companion.EXTRA_USER
import com.example.githubuser.data.db.UserHelper
import com.example.githubuser.data.entity.User
import com.example.githubuser.data.helper.MappingHelper
import java.net.URL


internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()

    private lateinit var userHelper: UserHelper

    private var listUser = ArrayList<User>()

    override fun onCreate() {

    }

    private fun coba() {
        userHelper = UserHelper.getInstance(mContext.applicationContext)
        userHelper.open()

        val cursor1 = userHelper.queryAll()
        val mapping = MappingHelper.mapCursorToArrayList(cursor1)

        Log.d("COBA3", mapping.toString())

        if (mapping.size > 0) {
            listUser.addAll(mapping)
        }
        else{
            listUser = ArrayList()
        }
    }

    override fun onDataSetChanged() {
        //Ini berfungsi untuk melakukan refresh saat terjadi perubahan.
        userHelper = UserHelper.getInstance(mContext.applicationContext)
        userHelper.open()

        coba()
        Log.d("LIST USER", listUser.toString())
        for ( user in listUser){
            val url = URL(user.avatar_url)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            mWidgetItems.add(bmp)
//            Log.d("LIST USER", user.html_url)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, com.example.githubuser.R.layout.widget_item)
        rv.setImageViewBitmap(com.example.githubuser.R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImagesBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        fillInIntent.putExtra(EXTRA_USER, listUser[position].login)

        rv.setOnClickFillInIntent(com.example.githubuser.R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}