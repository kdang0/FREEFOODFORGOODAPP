package com.example.freefoodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.*
import java.util.*

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var DB: DatabaseReference
    lateinit var DBPosts: DatabaseReference
    lateinit var DBComments: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = FirebaseDatabase.getInstance().reference
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        DBComments = DB.child(DatabaseVars.FIREBASE_COMMENTS)

        DBPosts.orderByKey().addChildEventListener(postListener)
        DBPosts.orderByKey().addChildEventListener(commentListener)
    }

    val postListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
            addPostToAList(dataSnapshot)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "postListener error: ", error.toException())
        }
    }

    val commentListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
            addCommentToAList(dataSnapshot)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "commentListener error: ", error.toException())
        }
    }

    private fun addPostToAList(dataSnapshot: DataSnapshot) {
        val post = Post.createPost()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        Log.d(TAG, "map contans: $map")

        post.id = dataSnapshot.key
        post.user = map.get("user") as String?
        //post.date = map.get("date") as Date?
        post.location = map.get("location") as String?
        post.likes = map.get("likes") as Long?
        post.image = map.get("image") as String?
        post.name = map.get("name") as String?
        post.description = map.get("description") as String?
        //toDoItemList!!.add(todoItem);
        //Put the new post somewhere
        Log.d(TAG, "New post added: ${post.id}")
       // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    private fun addCommentToAList(dataSnapshot: DataSnapshot) {
        val comment = Comment.createComment()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        comment.id = dataSnapshot.key
        //comment.date = map.get("date") as Date?
        comment.postID = map.get("postID") as String?
        comment.content = map.get("content") as String?
        comment.user = map.get("user") as String?
        //toDoItemList!!.add(todoItem);
        //Put the new post somewhere
        Log.d(TAG, "New comment added: ${comment.id}")
        // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    fun createPost(description: String, name: String, image: String, location: String, user: String) {
        val post = Post.createPost()
        post.date = Date()
        post.description = description
        post.name = name
        post.image = image
        post.likes = 0
        post.location = location
        post.user = user

        val newPost = DB.child(DatabaseVars.FIREBASE_POSTS).push()
        post.id = newPost.key
        newPost.setValue(post)
        Log.d(TAG, "Post uploaded to cloud")
    }

    fun createComment(content: String, user: String, postID: String) {
        val comment = Comment.createComment()
        comment.date = Date()
        comment.content = content
        comment.user = user
        comment.postID = postID

        val newComment = DB.child(DatabaseVars.FIREBASE_COMMENTS).push()
        comment.id = newComment.key
        newComment.setValue(comment)
        Log.d(TAG, "Comment uploaded to cloud")
    }
}