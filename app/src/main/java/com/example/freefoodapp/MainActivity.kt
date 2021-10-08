package com.example.freefoodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var DB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = FirebaseDatabase.getInstance().reference
        //createPost()
        //createComment()
    }

    fun createPost() {
        val post = Post.createPost()
        post.date = Date()
        post.description = "Description description replace with textview text"
        post.name = "TEST NAME. Replace with TextView text"
        post.image = "Previously uploaded image location"
        post.likes = 0
        post.location = "Test Location. Replace with actual location from textview text"
        post.user = "CurrentUserThisIsAnExampleUsername"

        val newPost = DB.child(DatabaseVars.FIREBASE_POSTS).push()
        post.id = newPost.key
        newPost.setValue(post)
    }

    fun createComment() {
        val comment = Comment.createComment()
        comment.date = Date()
        comment.content = "Description description replace with textview text"
        comment.user = "CurrentUserThisIsAnExampleUsername"
        comment.postID = "-MlSLcizbRZ5AMOgi8yA" //Replace with the ID of the curerntly selected post

        val newComment = DB.child(DatabaseVars.FIREBASE_COMMENTS).push()
        comment.id = newComment.key
        newComment.setValue(comment)
    }
}