package com.example.freefoodapp.firebase

import java.util.*

class Post {

    companion object Creater {
        fun createPost(): Post = Post()
    }
    var id: String? = null
    var date: Date? = null
    var description: String? = null
    var image: String? = null
    var likes: Int? = null
    var location: String? = null
    var name: String? = null
    var user: String? = null
}