package com.kunal.neptune.utils

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val images:List<Image>,
    val videos:List<Video>
)
