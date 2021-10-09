package com.example.freefoodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener




const val TAG = "MainActivity"

//register vars
private var firstName: String? = null
private var lastName: String? = null
private var email: String? = null
private var password: String? = null

//login vars
private var loginEmail: String? = null
private var loginPassword: String? = null
var globalFirstName: String? = null
var globalLastName: String? = null

class MainActivity : AppCompatActivity() {

    lateinit var DB: DatabaseReference
    lateinit var DBPosts: DatabaseReference
    lateinit var DBComments: DatabaseReference
    lateinit var DBUsers: DatabaseReference
    lateinit var DBAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = FirebaseDatabase.getInstance().reference
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        DBComments = DB.child(DatabaseVars.FIREBASE_COMMENTS)
        DBAuth = FirebaseAuth.getInstance()
        DBUsers = DB.child("Users")

        login("TestEmail@kylem.org", "123456")

        DBPosts.orderByKey().addChildEventListener(postListener)
        DBPosts.orderByKey().addChildEventListener(commentListener)
    }

    val postListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded posts:" + dataSnapshot.key!!)
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
            Log.d(TAG, "onChildAdded comments:" + dataSnapshot.key!!)
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

    private fun createAccount() {
        Log.d(TAG, "Creating new account...")
        firstName = "TestFirst"
        lastName = "TestLast"
        email = "TestEmail@kylem.org"
        password = "123456"

        if(!firstName!!.isEmpty() && !lastName!!.isEmpty() && !email!!.isEmpty() && !password!!.isEmpty()) {
            DBAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Creating user worked!")
                        val userId = DBAuth!!.currentUser!!.uid
                        val currentUserDb = DBUsers!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)
                        globalFirstName = firstName
                        globalLastName = lastName
                    } else {
                        Log.w(TAG, "Creating user failed: ", task.exception)
                    }
                }
        }
        else
        {
            Log.d(TAG, "A field was empty when trying to create an account")
        }
        Log.d(TAG, "Creating done for new account...")
    }

    private fun login(givenEmail: String, givenPassword: String) {
        loginEmail = givenEmail
        loginPassword = givenPassword
        if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
            Log.d(TAG, "Trying to login.")
            DBAuth.signInWithEmailAndPassword(loginEmail!!, loginPassword!!).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "LOGIN SUCCESS!")
                    //Do something here
                    Log.d(TAG, "Logged in User ID: ${task.result?.user?.uid}")
                    var temp = DBUsers.child("${task.result?.user?.uid}")
                    temp.addValueEventListener(userEventListener)
                }
                else {
                    Log.d(TAG, "LOGIN FAILED: ${task.exception}")
                    //Tell user here
                }
            }
        }
        else {
            Log.d(TAG, "Login or password is empty")
            //Tell user here
        }
    }

    /**
     * This is to get Firstname and Lastname once the user logs in
     */
    var userEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "UserEventListener: $snap")
                if(snap.key.equals("firstName")) {
                    globalFirstName = "${snap.getValue()}"
                    Log.d(TAG, "Firstname: $globalFirstName")
                }
                if(snap.key.equals("lastName")) {
                    globalLastName = "${snap.getValue()}"
                    Log.d(TAG, "Lastname: $globalLastName")
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun addPostToAList(dataSnapshot: DataSnapshot) {
        val post = Post.createPost()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        Log.d(TAG, "map contans: $map")

        post.id = dataSnapshot.key
        post.user = map.get("user") as String?
        var dateMap = map.get("date") as HashMap<String, Any>

        var tempDate = Date()

        tempDate.time = (dateMap.get("time") as Long)
        post.date = tempDate
        Log.d(TAG, "dateMap: $dateMap")
        Log.d(TAG, "createdDate: $tempDate")

        post.location = map.get("location") as String?
        post.likes = map.get("likes") as Long?
        post.image = map.get("image") as String?
        post.name = map.get("name") as String?
        post.description = map.get("description") as String?
        //toDoItemList!!.add(todoItem);
        //Put the new post somewhere
        Log.d(TAG, "New post added: $post.id}")
       // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    private fun addCommentToAList(dataSnapshot: DataSnapshot) {
        val comment = Comment.createComment()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        comment.id = dataSnapshot.key
        var dateMap = map.get("date") as HashMap<String, Any>
        var tempDate = Date()
        tempDate.time = (dateMap.get("time") as Long)
        comment.date = tempDate
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