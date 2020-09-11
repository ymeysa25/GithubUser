package com.example.githubuser.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.githubuser.R

class Splashscreen : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)


        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, ListUserActivity::class.java)
            startActivity(intent)
            finish()
        },
            3000
        )
    }
}
