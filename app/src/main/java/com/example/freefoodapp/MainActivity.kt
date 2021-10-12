package com.example.freefoodapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

private const val TAG = "MainActivity"

//register vars
private var username: String? = null
private var email: String? = null
private var password: String? = null

//login vars
private var loginEmail: String? = null
private var loginPassword: String? = null
var globalUserName: String? = null

//Image Upload vars
private var firebaseStore: FirebaseStorage? = null
var storageRef: StorageReference? = null
var uploadableFilePath: String? = null

class MainActivity : AppCompatActivity(), LoginFragment.MainCallbacks, RegistrationFragment.MainCallbacks, ConfirmationFragment.MainCallbacks,
    FoodListFragment.MainCallbacks, FoodEventCreateFragment.MainCallbacks, FoodEventFragment.MainCallbacks {

    lateinit var DB: DatabaseReference //Registering
    lateinit var DBPosts: DatabaseReference
    lateinit var DBComments: DatabaseReference
    lateinit var DBUsers: DatabaseReference //Registering
    lateinit var DBAuth: FirebaseAuth //Registering
    lateinit var DBLikeRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        DBComments = DB.child(DatabaseVars.FIREBASE_COMMENTS)
        firebaseStore = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        DBAuth = FirebaseAuth.getInstance() //registering
        DBUsers = DB.child("Users") //registering

        //login("TestEmail2@kylem.org", "123456")
        //addLikeToPost("-MlSLcizbRZ5AMOgi8yA")
        //removeLikeToPost("-MlSLcizbRZ5AMOgi8yA")
        //createPost("This is a test post", "Cheese at fountain", "test", "DAKA", "iGJahlqUBTPI0ipaydHnmmNERnC3")
        //deletePostByID("-MlSLcizbRZ5AMOgi8yA")

        DBComments.orderByKey().addChildEventListener(commentListener)


        //createNewAccount()
        if (currentFragment == null) {
            val fragment = LoginFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    val commentListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded comments:" + dataSnapshot.key!!)
            addCommentToAList(dataSnapshot)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            //When post updates, such as the likes change
            Log.d(TAG, "COMMENT HAS RECIEVED AN UPDATE: " + snapshot.key)
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
        username = "TestFirst"
        email = "TestEmail2@kylem.org"
        password = "123456"

        if(!username!!.isEmpty() && !email!!.isEmpty() && !password!!.isEmpty()) {
            DBAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Creating user worked!")
                        val userId = DBAuth!!.currentUser!!.uid
                        val currentUserDb = DBUsers!!.child(userId)
                        currentUserDb.child("username").setValue(username)
                        globalUserName = username
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
                if(snap.key.equals("username")) {
                    globalUserName = "${snap.getValue()}"
                    Log.d(TAG, "Username: $globalUserName")
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun addLikeToPost(givenPostID: String) {
        DBLikeRef = DB.child(DatabaseVars.FIREBASE_POSTS).child(givenPostID)
        DBLikeRef.addListenerForSingleValueEvent(likeListener)
    }

    private fun removeLikeToPost(givenPostID: String) {
        DBLikeRef = DB.child(DatabaseVars.FIREBASE_POSTS).child(givenPostID)
        DBLikeRef.addListenerForSingleValueEvent(likeRemoveListener)
    }

   var likeListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var currentLikes: Int? = null
            Log.d(TAG, "LIKES DATASNAP: $dataSnapshot")
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "LIKES SNAP: $snap")
                if(snap.key.equals("likes")) {
                    currentLikes = (snap.value as Long).toInt()
                }
            }
            Log.d(TAG, "Found Likes: $currentLikes")
            //Now update it
            if(currentLikes != null) {
                var newLikes = currentLikes!! + 1
                DBLikeRef.child("likes").setValue(newLikes)
            }
        }

       override fun onCancelled(databaseError: DatabaseError) {}
    }

    var likeRemoveListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var currentLikes: Int? = null
            Log.d(TAG, "LIKES DATASNAP REMOVE: $dataSnapshot")
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "LIKES SNAP REMOVE: $snap")
                if(snap.key.equals("likes")) {
                    currentLikes = (snap.value as Long).toInt()
                }
            }
            Log.d(TAG, "Found Likes: $currentLikes")
            //Now update it
            if(currentLikes != null) {
                var newLikes = currentLikes!! - 1
                DBLikeRef.child("likes").setValue(newLikes)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
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

    //The filepath will be loated in uploadableFilePath
    fun createPost(description: String, name: String, image: String, location: String, user: String) {
        var date = Date()
        var description = description
        var name = name
        var image = image
        var likes:Long = 0
        var location = location
        var user = user
        val newPost = DB.child(DatabaseVars.FIREBASE_POSTS).push()
        var id = newPost.key

        val post = Post(id,date,description,image,likes,location,name,user)
        newPost.setValue(post)
        Log.d(TAG, "Post uploaded to cloud")
    }

    private fun uploadImageToFirebase(pathToUploadFile: Uri){
        if(pathToUploadFile != null){
            val reference = storageRef?.child("postImgUploads/" + UUID.randomUUID().toString())
            val uploadImgTask = reference?.putFile(pathToUploadFile!!)
            val uploadUrlTask = uploadImgTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation reference.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uri = task.result
                    //Do something with the uri
                    uploadableFilePath = uri.toString()
                    //TODO: Trigger something here to actually post. Possible call the createPost() here?
                } else {
                    Log.d(TAG, "An error occured uploading a file")
                }
            }?.addOnFailureListener{
                //Put something here for another failure?
            }
        }else{
            Log.d(TAG, "You need to upload an image")
        }
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

        var d: Date = Date()

    }

    fun deletePostByID(postID: String) {
        DBPosts.child(postID).removeValue()
    }

    override fun onClickRegister() {
        val fragment = RegistrationFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onLogin(accountName: String, userName: String) {
        val fragment = FoodListFragment.newInstance(accountName, userName)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onRegister() {
        val fragment = ConfirmationFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onReturnToLogin() {
        val fragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onEventSelected(email: String, userName: String, post: Post) {
        val fragment = FoodEventFragment.newInstance(email, userName, post)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onPost(accountName: String, userName: String) {
        val fragment = FoodListFragment.newInstance(accountName, userName)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onGoToComments(userName: String, postID: String) {
        Log.d(TAG, "Go to comments called.")
        val fragment = CommentsFragment.newInstance(userName, postID)
        Log.d(TAG, "New comments created.")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onCreateEvent(email: String, userName: String) {
        val fragment = FoodEventCreateFragment.newInstance(email, userName)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}