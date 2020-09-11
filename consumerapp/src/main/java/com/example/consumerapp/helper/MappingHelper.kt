package com.example.consumerapp.helper

import android.database.Cursor
import com.example.consumerapp.db.DatabaseContract
import com.example.consumerapp.entity.User


object MappingHelper {

    fun mapCursorToArrayList(usersCursor: Cursor?): ArrayList<User> {
        val userList = ArrayList<User>()
        usersCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.HTMLURL))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
//                val date = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.DATE))
                userList.add(User(id, title, description, avatar))
            }
        }
        return userList
    }

    fun mapCursorToObject(usersCursor: Cursor?): User {
        var user = User()
        usersCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
            val title = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
            val description = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.HTMLURL))
            val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
            user = User(id, title, description, avatar)
        }
        return user
    }
}