package com.example.githubuser.UI.UserDetails

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.githubuser.R
import com.example.githubuser.UI.ListUserActivity
import com.example.githubuser.data.adapter.UserAdapter
import com.example.githubuser.data.db.DatabaseContract
import com.example.githubuser.data.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.example.githubuser.data.db.UserHelper
import com.example.githubuser.data.entity.User
import com.example.githubuser.data.helper.MappingHelper
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_user.*
import kotlinx.android.synthetic.main.activity_detail_user.tv_username
import kotlinx.android.synthetic.main.list_user.*
import org.json.JSONObject


class DetailUserActivity : AppCompatActivity() {


    private lateinit var userAvatar: ImageView
    private var user: User? = null
    private var position: Int = 0
    private lateinit var userHelper: UserHelper

//    private var userList = ArrayList<User>()

    private lateinit var uriWithId: Uri

    companion object {
        private val TAG = DetailUserActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        userHelper = UserHelper.getInstance(applicationContext)
        userHelper.open()

        user = intent.getParcelableExtra(ListUserActivity.EXTRA_USER)

        if (user != null) {
            position = intent.getIntExtra(ListUserActivity.EXTRA_POSITION, 0)
        } else {
            user = User()
        }

        progressBar.visibility = View.VISIBLE
        val bundle: Bundle? = intent.extras
        val username = bundle!!.getString(UserAdapter.USER_KEY)

        tv_username.text = username
        getUser(username)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(this, supportFragmentManager)

        sectionsPagerAdapter.username = username
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f

        var statusFav = if (username != null) loadStatusFav(username) else 0
        // masih belum sesuai database
        setStatusFav(statusFav)

        fab_fav_user.setOnClickListener {
            if (statusFav == 0) {
                addFav(user!!)
            } else {
                username?.let { it1 -> deleteFav(it1) }
            }
            statusFav = changevalue(statusFav)
            setStatusFav(statusFav)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun loadStatusFav(username: String): Int {

        var uriWithUsername = Uri.parse(CONTENT_URI.toString() + "/" + username)

        val cursor = contentResolver.query(uriWithUsername, null, null, null, null)
        val test = MappingHelper.mapCursorToArrayList(cursor)


//        Log.d("Hasil Simpan",test.toString())
        return if (test.size > 0) 1 else 0
    }

    private fun changevalue(int: Int): Int {
        val value = when (int) {
            1 -> 0
            0 -> 1
            else -> 10
        }
        return value
    }

    private fun deleteFav(username: String) {
        var uriWithUsername = Uri.parse(CONTENT_URI.toString() + "/" + username)
        contentResolver.delete(uriWithUsername, null, null)
    }

    private fun addFav(user: User) {
        val user_name = tv_username.text.toString()
        val html = tv_html.text.toString()
        val avatar = tv_avar_url.text.toString()

        val values = ContentValues()
        values.put(DatabaseContract.UserColumns.USERNAME, user_name)
        values.put(DatabaseContract.UserColumns.HTMLURL, html)
        values.put(DatabaseContract.UserColumns.AVATAR, avatar)
        values.put(DatabaseContract.UserColumns.FAVSTATUS, 1)

//        val result = userHelper.insert(values)

        contentResolver.insert(CONTENT_URI, values)


//        user.id = result.toInt()
//        setResult(NoteAddUpdateActivity.RESULT_ADD)
//        finish()
    }

    private fun setStatusFav(statusFav: Int) {
        if (statusFav == 1) {
            fab_fav_user.setImageResource(R.drawable.fav)
        } else {
            fab_fav_user.setImageResource(R.drawable.not_fav)
        }
    }


    private fun getUser(username: String?) {
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username"
        client.addHeader("Authorization", "token 70d3536b2327ef47ad67da61aa07332163f2f941")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                try {
                    val responseObject = JSONObject(result)
                    val name = responseObject.getString("name")
                    val avatarurl = responseObject.getString("avatar_url")
                    val location = responseObject.getString("location")
                    val followers: Int? = responseObject.optInt("followers")
                    val following = responseObject.getInt("following")
                    val publicrepos = responseObject.getInt("public_repos")


                    tv_name.text = name
                    tv_address.text = location
                    tv_repo.text = publicrepos.toString()
                    tv_follower.text = followers.toString()
                    tv_following.text = following.toString()
                    tv_avar_url.text = avatarurl.toString()

                    userAvatar = findViewById(R.id.img_user)
                    Glide.with(this@DetailUserActivity).load(avatarurl).into(userAvatar)

                } catch (e: Exception) {
                    Toast.makeText(this@DetailUserActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                // Jika koneksi gagal
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@DetailUserActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}