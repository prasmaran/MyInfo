package com.example.myinfo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUser(
    val firstName: String,
    val username: String
) : Parcelable