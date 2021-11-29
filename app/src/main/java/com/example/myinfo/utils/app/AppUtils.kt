package com.example.myinfo.utils.app

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppUtils {

    companion object {

        // Toast message function
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        // Snack bar message function
        fun View.showSnackBar(msg: String) {
            Snackbar.make(this, msg, Snackbar.LENGTH_LONG).also { snackbar ->
                snackbar.setAction("OKAY") {
                    snackbar.dismiss()
                }
            }.show()
        }

        // Custom dialog fragment function
        fun showDialogFragment(
            dialogFragment: DialogFragment,
            fragmentManager: FragmentManager,
            fragTag: String
        ) {
            val ft: FragmentTransaction = fragmentManager.beginTransaction()
            val prev: Fragment? =
                fragmentManager.findFragmentByTag(fragTag)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, fragTag)
        }

        @SuppressLint("Range")
        fun ContentResolver.getFileName(uri: Uri): String {
            var fileName = ""
            val cursor = query(uri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                fileName = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
            return fileName
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateTimestampUtil(dateTime: String): String {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
        }

    }

}