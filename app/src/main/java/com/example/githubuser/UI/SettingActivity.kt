package com.example.githubuser.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.githubuser.R
import com.example.githubuser.alarm.AlarmReceiver
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var alarmReceiver: AlarmReceiver
//    private lateinit var swReminder: Switch

    companion object {
        const val SHARED_PREFS = "TOOGLE_STATUS"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        alarmReceiver = AlarmReceiver()
        onSwitch()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun onSwitch() {

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        switchReminder.isChecked = sharedPreferences.getBoolean(SHARED_PREFS, false)
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                editor.putBoolean(SHARED_PREFS, true)
                val repeatTime = "09:00"
                val repeatMessage = getString(R.string.alarm_reminder)
                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING,
                    repeatTime, repeatMessage)
            } else {
                editor.putBoolean(SHARED_PREFS, false)
                alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
            }
            editor.apply()
        }
    }


}

