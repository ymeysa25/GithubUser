package com.example.githubuser.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.githubuser.data.db.DatabaseContract.AUTHORITY
import com.example.githubuser.data.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.example.githubuser.data.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.example.githubuser.data.db.UserHelper

class UserProvider : ContentProvider() {

    companion object {
        private const val USER = 1
        private const val USER_ID = 2
        private const val USER_FAV = 3
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var userHelper: UserHelper
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, USER)

            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", USER_ID)

            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/*", USER_FAV)
        }
    }

    override fun onCreate(): Boolean {
        userHelper = UserHelper.getInstance(context as Context)
        userHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor:Cursor?
        when(sUriMatcher.match(uri)){
            USER -> cursor = userHelper.queryAll()
            USER_ID -> cursor = userHelper.queryById(uri.lastPathSegment.toString())
            USER_FAV -> cursor = userHelper.queryByUsername(uri.lastPathSegment.toString())
            else -> cursor = null
        }

        return cursor
    }


    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (USER) {
            sUriMatcher.match(uri) -> userHelper.insert(contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val updated: Int = when (USER_ID) {
            sUriMatcher.match(uri) -> userHelper.update(uri.lastPathSegment.toString(),contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (sUriMatcher.match(uri)) {
            USER_ID -> userHelper.deleteById(uri.lastPathSegment.toString())
            USER_FAV -> userHelper.deletebByUsername(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}
