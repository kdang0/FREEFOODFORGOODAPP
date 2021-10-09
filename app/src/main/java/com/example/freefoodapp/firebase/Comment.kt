package com.example.freefoodapp.firebase

import java.util.*

class Comment {

    companion object Creater {
        fun createComment(): Comment = Comment()
    }
    var id: String? = null
    var date: Date? = null
    var content: String? = null
    var user: String? = null
    var postID: String? = null
}