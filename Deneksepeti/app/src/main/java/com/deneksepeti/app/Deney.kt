package com.deneksepeti.app

import java.util.*
import kotlin.collections.ArrayList

class Deney (
    var image: String, var title: String, var displayName: String,
    var description: String, var quota: String, var deadline: Date, var postId: String,
    var users: ArrayList<String>, var userId: String, var requirements: String
) {
    fun toCard() = Card(
        image = image,
        title = title,
        desc = description,
        quota = quota,
        dayleft = deadLineToDays()
    )

    fun deadLineToDays(): String {
        val now = Date().time

        val diffInDays = ( ( deadline.time - now )
                / (1000 * 60 * 60 * 24) )

        return when {
            diffInDays < 0 -> "Applications closed"
            diffInDays < 1 -> "<1 day left"
            else        -> "${diffInDays} day left"
        }
    }
}