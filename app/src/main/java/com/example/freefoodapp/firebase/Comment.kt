package com.example.freefoodapp.firebase

import java.util.*

/**
 * Comment is the comments on the app
 */
class Comment {

    /**
     * Function to create the comment, and then we can manually set values
     */
    companion object Creater {
        fun createComment(): Comment = Comment()
    }
    var id: String? = null //UUID of comment
    var date: Date? = null // Date the comment was posted
    var content: String? = null //The body of the comment
    var user: String? = null //Username of the comment creater
    var postID: String? = null //UUID of the referenced POST
}