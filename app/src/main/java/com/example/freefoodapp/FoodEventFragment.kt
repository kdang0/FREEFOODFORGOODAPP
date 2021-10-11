package com.example.freefoodapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "FoodEventFragment"
private const val ARG_EMAIL = "User_Email"
private const val ARG_USERNAME = "User_Username"
private const val ARG_POST = "Post"

class FoodEventFragment: Fragment() {
    private lateinit var viewdesc: TextView
    private lateinit var description: TextView
    private lateinit var location: TextView
    private lateinit var eventLocation: TextView
    private lateinit var eventName: TextView
    private lateinit var eventDateTime: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var eventImage: ImageView
    private lateinit var likeEvent: ImageButton
    private lateinit var dislikeEvent: ImageButton
    private lateinit var commentEvent: Button
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBPosts: DatabaseReference
    private var email: String = ""
    private var username: String = ""
    private lateinit var post: Post

    interface MainCallbacks {
        fun onGoToComments(userName: String, postID: String)
    }
    private var mainCallbacks: MainCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        email = arguments?.getSerializable(ARG_EMAIL) as String
        post = arguments?.getSerializable(ARG_POST) as Post
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainCallbacks = context as FoodEventFragment.MainCallbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        commentEvent.setOnClickListener {
            post.id?.let { it1 -> mainCallbacks?.onGoToComments(username, it1) }
        }
        likeEvent.setOnClickListener {
            //increase likes by one
        }
        dislikeEvent.setOnClickListener {
            //decrease likes by one
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(accountName: String, userName: String, post: Post): FoodEventFragment {
            val args = Bundle().apply {
                putSerializable(ARG_EMAIL, accountName)
                putSerializable(ARG_USERNAME, userName)
                putSerializable(ARG_POST, userName)
            }
            return FoodEventFragment().apply {
                arguments = args
            }
        }
    }
}