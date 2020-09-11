package com.example.githubuser.UI.UserDetails


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.githubuser.R
import com.example.githubuser.data.entity.User
import com.example.githubuser.data.adapter.UserAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

/**
 * A simple [Fragment] subclass.
 */
class DetailUserFragmentFollowing : Fragment() {

    private lateinit var rvItem : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_user_fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(UserAdapter.USER_KEY)
        rvItem = view.findViewById(R.id.rv_item_following)
        rvItem.setHasFixedSize(true)

        getFollowerUserFromAPI(name)
    }

    private fun showRecyclerList( list : ArrayList<User>){
        rvItem.layoutManager = LinearLayoutManager(context )
        val userAdapter = UserAdapter(list)
        rvItem.adapter = userAdapter
    }

    private fun getFollowerUserFromAPI(username: String?) {
        val listUser = ArrayList<User>()
//        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/following"
        client.addHeader("Authorization", "token 70d3536b2327ef47ad67da61aa07332163f2f941")
        client.addHeader("User-Agent","request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
//                progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)
                try {
                    val resultArray = JSONArray(result)
                    for (i in 0 until resultArray.length()) {
                        val jsonObject = resultArray.getJSONObject(i)
                        val username = jsonObject.getString("login")
                        val html_url = jsonObject.getString("html_url")
                        val img : String = jsonObject.getString("avatar_url")
                        val user = User()
                        user.login = username
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
//                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(DetailUserActivity(),errorMessage, Toast.LENGTH_SHORT).show()

            }
        } )
    }

    companion object {

        fun newInstance(username: String): DetailUserFragmentFollowing {
            val fragment = DetailUserFragmentFollowing()
            val bundle = Bundle()
            bundle.putString(UserAdapter.USER_KEY, username)
            fragment.arguments = bundle
            return fragment
        }
    }


}
