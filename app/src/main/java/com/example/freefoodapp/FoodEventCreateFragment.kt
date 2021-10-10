package com.example.freefoodapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

private const val TAG = "FoodEventCreateFragment"
private const val ARG_EMAIL = "User_Email"

class FoodEventCreateFragment: Fragment() {
    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageTextView: TextView
    private lateinit var eventImage: ImageView
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var location: EditText
    private lateinit var description: EditText
    private lateinit var eventName: EditText
    private lateinit var postEvent: Button
    private lateinit var uploadImage: Button
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBPosts: DatabaseReference
    private var name: String = ""
    private var date: String = ""
    private var time: String = ""
    private var loc: String = ""
    private var descrip: String = ""

    interface MainCallbacks {
        fun onPost()
    }
    private var mainCallbacks: MainCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
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
        val view = inflater.inflate(R.layout.create_foodpost, container, false)
        nameTextView = view.findViewById(R.id.nameTextView) as TextView
        descriptionTextView = view.findViewById(R.id.descriptionTextView) as TextView
        imageTextView = view.findViewById(R.id.imageTextView) as TextView
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView) as TextView
        locationTextView = view.findViewById(R.id.locationTextView) as TextView
        eventImage = view.findViewById(R.id.eventImage) as ImageView
        location = view.findViewById(R.id.location) as EditText
        editTextDate = view.findViewById(R.id.editTextDate) as EditText
        editTextTime = view.findViewById(R.id.editTextTime) as EditText
        eventName = view.findViewById(R.id.eventName) as EditText
        description = view.findViewById(R.id.description) as EditText
        postEvent = view.findViewById(R.id.postEvent) as Button
        uploadImage = view.findViewById(R.id.uploadPost) as Button
        postEvent.setOnClickListener {
            var something: String = ""
            var somethingUser: String = ""
            createPost(descrip, name, something, loc, somethingUser)
            mainCallbacks?.onPost()
        }
        uploadImage.setOnClickListener {
            //image stuff
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()
        val nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                name = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        eventName.addTextChangedListener(nameTextWatcher)
        val descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                descrip = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        description.addTextChangedListener(descriptionTextWatcher)
        val locationTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                loc = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        location.addTextChangedListener(locationTextWatcher)
        val dateTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                date = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        editTextDate.addTextChangedListener(dateTextWatcher)
        val timeTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                time = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        editTextTime.addTextChangedListener(timeTextWatcher)
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

    companion object {
        fun newInstance(accountName: String): FoodListFragment {
            val args = Bundle().apply {
                putSerializable(ARG_EMAIL, accountName)
            }
            return FoodListFragment().apply {
                arguments = args
            }
        }
    }
}