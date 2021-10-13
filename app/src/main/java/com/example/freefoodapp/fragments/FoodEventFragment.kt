package com.example.freefoodapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.freefoodapp.R
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.*

private const val TAG = "FoodEventFragment"
private const val ARG_EMAIL = "User_Email" //Email argment passed
private const val ARG_USERNAME = "User_Username" //Username argment passsed
private const val ARG_POST = "Post" //Post argument passed

class FoodEventFragment: Fragment() {
    private lateinit var viewdesc: TextView  //Description title
    private lateinit var description: TextView //Actual description
    private lateinit var location: TextView //location title
    private lateinit var eventLocation: TextView //Actual location
    private lateinit var eventName: TextView //Event title
    private lateinit var eventDateTime: TextView //Event date actual
    private lateinit var dateTextView: TextView //Date title
    private lateinit var timeTextView: TextView //Time actual
    private lateinit var eventImage: ImageView //Image view of event's image
    private lateinit var likeEvent: ImageButton // Image button to add like
    private lateinit var dislikeEvent: ImageButton //Image button to add dislike
    private lateinit var commentEvent: Button //Button to allow user to get to comment fragment
    lateinit var DB: DatabaseReference //Firebase database reference
    lateinit var DBPosts: DatabaseReference //Firebase posts reference
    private var email: String = "" //email of user
    private var username: String = "" //username of user
    private lateinit var post: Post //Post object retrieved
    lateinit var DBLikeRef: DatabaseReference //Reference to the life reference

    interface MainCallbacks {
        fun onGoToComments(userName: String, postID: String, postName: String)
    }
    private var mainCallbacks: MainCallbacks? = null

    /**
     * initalize data and references
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        email = arguments?.getSerializable(ARG_EMAIL) as String
        post = arguments?.getParcelable<Post>(ARG_POST) as Post
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainCallbacks = context as MainCallbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * Initalize reference to all the UI elements
         */
        val view = inflater.inflate(R.layout.view_foodevent, container, false)
        viewdesc = view.findViewById(R.id.viewdesc) as TextView
        description = view.findViewById(R.id.description) as TextView
        eventLocation = view.findViewById(R.id.eventLocation) as TextView
        eventDateTime = view.findViewById(R.id.eventDateTime) as TextView
        eventName = view.findViewById(R.id.eventName) as TextView
        location = view.findViewById(R.id.location) as TextView
        dateTextView = view.findViewById(R.id.dateTextView) as TextView
        timeTextView = view.findViewById(R.id.timeTextView) as TextView
        eventImage = view.findViewById(R.id.eventImage) as ImageView
        likeEvent = view.findViewById(R.id.likeEvent) as ImageButton
        dislikeEvent = view.findViewById(R.id.dislikeEvent) as ImageButton
        commentEvent = view.findViewById(R.id.commentEvent) as Button
        description.setText(post.description)
        dateTextView.setText(post.date.toString())
        timeTextView.setText(post.date.toString())
        location.setText(post.location)
        eventName.setText(post.name)
        Log.d(TAG, "Email is: $email")
        commentEvent.setOnClickListener {
            /**
             * Send user to comments with the appropriate data (postID, username, name)
             */
            Log.d(TAG, "email is: $email")
            Log.d(TAG, "Username is: $username")
            Log.d(TAG, "Comment Clicked")
            var id: String? = post.id
            var name: String? = post.name
            if (id != null && name != null) {
                mainCallbacks?.onGoToComments(username, id, name)
            }
        }
        likeEvent.setOnClickListener {
            /**
             * When clicking like, disable other buttons from being pressed again (no double likeing/disliking)
             * Send Toast to user telling them they liked it
             */
            post.id?.let { it1 -> addLikeToPost(it1) }
            likeEvent.isEnabled = false
            dislikeEvent.isEnabled = false
            Toast.makeText(activity, "Like Added!", Toast.LENGTH_SHORT).show()
        }
        dislikeEvent.setOnClickListener {
            /**
             * When clicking dislike, disable other buttons from being pressed again (no double likeing/disliking)
             * Send Toast to user telling them they disliked it
             */
            post.id?.let { it1 -> removeLikeToPost(it1) }
            dislikeEvent.isEnabled = false
            likeEvent.isEnabled = false
            Toast.makeText(activity, "Dislike Added!", Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, post.image.toString())
        Glide.with(eventImage)
            .load(post.image)
            .into(eventImage) //This loads the image view with the image referenced in the URL
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * When the user likes, this should add a listener
     */
    private fun addLikeToPost(givenPostID: String) {
        DBLikeRef = DB.child(DatabaseVars.FIREBASE_POSTS).child(givenPostID)
        DBLikeRef.addListenerForSingleValueEvent(likeListener)
    }

    /**
     * When the userlikes, this should add a listener
     */
    private fun removeLikeToPost(givenPostID: String) {
        DBLikeRef = DB.child(DatabaseVars.FIREBASE_POSTS).child(givenPostID)
        DBLikeRef.addListenerForSingleValueEvent(likeRemoveListener)
    }

    /**
     * Function to handle changes to likes
     */
    var likeListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var currentLikes: Int? = null
            Log.d(TAG, "LIKES DATASNAP: $dataSnapshot")
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "LIKES SNAP: $snap")
                if(snap.key.equals("likes")) {
                    currentLikes = (snap.value as Long).toInt() //Get the current likes in the DB
                }
            }
            Log.d(TAG, "Found Likes: $currentLikes")
            //Now update it
            if(currentLikes != null) {
                var newLikes = currentLikes!! + 1 //Add one to the current likes in the DB
                DBLikeRef.child("likes").setValue(newLikes) //Update this value in the DB
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    /**
     * Function to handle what happens when a like is removed
     */
    var likeRemoveListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var currentLikes: Int? = null
            Log.d(TAG, "LIKES DATASNAP REMOVE: $dataSnapshot")
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "LIKES SNAP REMOVE: $snap")
                if(snap.key.equals("likes")) {
                    currentLikes = (snap.value as Long).toInt() //Get current likes
                }
            }
            Log.d(TAG, "Found Likes: $currentLikes")
            //Now update it
            if(currentLikes != null) {
                var newLikes = currentLikes!! - 1 //Remove one from the current likes
                DBLikeRef.child("likes").setValue(newLikes) //Update the DB
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    companion object {
        fun newInstance(accountName: String, userName: String, post: Post): FoodEventFragment {
            val args = Bundle().apply {
                Log.d(TAG, "Email is: $accountName")
                putSerializable(ARG_EMAIL, accountName)
                putSerializable(ARG_USERNAME, userName)
                putParcelable(ARG_POST, post)
            }
            return FoodEventFragment().apply {
                arguments = args
            }
        }
    }
}