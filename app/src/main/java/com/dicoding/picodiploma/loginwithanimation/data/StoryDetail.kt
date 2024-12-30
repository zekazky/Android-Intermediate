package com.dicoding.picodiploma.loginwithanimation.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryDetail(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String
) : Parcelable