package com.example.githubuser.UI

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubuser.R
import com.example.githubuser.data.adapter.UserAdapter
import com.example.githubuser.data.entity.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_list_user.*
import org.json.JSONObject

class ListUserActivity : AppCompatActivity() {

    private lateinit var rvItem : RecyclerView

    companion object{
        private val TAG = ListUserActivity::class.java.simpleName

        const val EXTRA_USER = "extra_user"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user)
        supportActionBar?.title = resources.getString(R.string.app_name)
        rvItem = findViewById(R.id.rv_item)
        rvItem.setHasFixedSize(true)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)



        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.mysearch).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                getDataUserFromAPI(query)
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
//                getDataUserFromAPI(newText)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.itfav ->{
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
            }
            R.id.itsetting ->{
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun showRecyclerList( list : ArrayList<User>){
        rvItem.layoutManager = LinearLayoutManager(this )
        val userAdapter = UserAdapter(list)
        rvItem.adapter = userAdapter
    }

    private fun getDataUserFromAPI(username: String?) {
        val listUser = ArrayList<User>()
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$username"
        client.addHeader("Authorization", "token 70d3536b2327ef47ad67da61aa07332163f2f941")
        client.addHeader("User-Agent","request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)
                try {
                    val resultObject = JSONObject(result)
                    val items = resultObject.getJSONArray("items")
                    for (i in 0 until items.length()) {
                        val jsonObject = items.getJSONObject(i)
                        val userlogin = jsonObject.getString("login")
                        val html_url = jsonObject.getString("html_url")
                        val img : String = jsonObject.getString("avatar_url")
                        val user = User()
                        user.login = userlogin
                        user.html_url = html_url
                        user.avatar_url = img
                        listUser.add(user)
                    }
                    showRecyclerList(listUser)
//                    Log.d(TAG, final_result.toString())

                } catch (e: Exception) {
                    //Toast.makeText(this@ListUserActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Testing Recycle", e.message)
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@ListUserActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        } )
    }
}