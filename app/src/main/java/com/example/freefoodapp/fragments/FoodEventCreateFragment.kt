package com.example.freefoodapp.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.freefoodapp.R
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.example.freefoodapp.getScaledBitmap
import com.example.freefoodapp.orientBitmap
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*

private const val TAG = "FoodEventCreateFragment"
private const val ARG_EMAIL = "User_Email" //Email passed in as argument
private const val ARG_USERNAME = "User_Username" //Username of user passed in as argument
private const val REQUEST_PHOTO = 2

class FoodEventCreateFragment: Fragment() {
    private lateinit var photoFile : File //Reference to the photo object
    private lateinit var photoUri : Uri //Reference to the path of the photo
    private lateinit var nameTextView: TextView //textview for title
    private lateinit var locationTextView: TextView //textview of the location
    private lateinit var dateTimeTextView: TextView //textview of the dateTime
    private lateinit var descriptionTextView: TextView //textview of the description
    private lateinit var imageTextView: TextView //textview of the image
    private lateinit var eventImage: ImageView //ImageView that will hold image
    private lateinit var editTextDate: EditText //TextEdit that will contain date
    private lateinit var editTextTime: EditText //EditText that will hold time
    private lateinit var location: EditText //EditText that will contain the user input location
    private lateinit var description: EditText //EditTest that will contain the user description
    private lateinit var eventName: EditText //EditText that will contain the user's Post title
    private lateinit var postEvent: Button //Button that will allow for posting
    private lateinit var uploadImage: Button //Button that will allow for uploading an image before posting
    lateinit var DB: DatabaseReference //Reference to Firebase
    lateinit var DBPosts: DatabaseReference //Reference to Firebase's child list of Posts
    private var name: String = "" //holds post title
    private var date: String = "" //holds post date
    private var time: String = "" //holds post time
    private var loc: String = "" //holds post location
    private var descrip: String = "" //holds post description
    private var email: String = "" //holds post email
    private var username: String = "" //holds post username
    private var uploadableFilePath: String? = null //holds the remote url to post
    private var storageRef: StorageReference? = null //holds the Firebase Storage reference

    interface MainCallbacks {
        fun onPost(email: String, userName: String)
    }
    private var mainCallbacks: MainCallbacks? = null

    /**
     * We initalize all the references to Database, pull arguments out
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        email = arguments?.getSerializable(ARG_EMAIL) as String
        storageRef = FirebaseStorage.getInstance().reference
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
         * Here we initalize the references to UI elements
         */
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
            //For post event button, sents all data to the post function
            createPost(descrip, name, uploadableFilePath!!, loc, email)
            mainCallbacks?.onPost(email, username)
        }
        postEvent.isEnabled = false
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoFile = File(context?.applicationContext?.filesDir, "IMG_EVENTPICTURE0.jpg") //initalize reference to locale photo file
        photoUri =  FileProvider.getUriForFile(requireActivity(),
            "com.example.freefoodapp", photoFile) //implicit intent to take photo for event

    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * Watch for all changes to the EditText fields, record it
     */
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

        /**
         * Send user over to implicit intent for photo taking,
         * granting all permissions in the process for the camera app and the return
         */
        uploadImage.apply {
            val packageManager : PackageManager =
                requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY)

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities : List<ResolveInfo> =
                    packageManager.queryIntentActivities(
                        captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)

                for(cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                    startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

    /**
     * Function to create post based off of all data passed in by user
     */
    fun createPost(description: String, name: String, image: String, location: String, user: String) {
        var date = Date()
        var description = description
        var name = name
        var image = image
        var likes:Long = 0
        var location = location
        var user = user
        val newPost = DB.child(DatabaseVars.FIREBASE_POSTS).push() //Add new post to DB without content to get key
        var id = newPost.key //get the UUID

        val post = Post(id,date,description,image,likes,location,name,user) //Create post object
        newPost.setValue(post) //Actually populate database object
        Log.d(TAG, "Post uploaded to cloud")
    }

    /**
     * Function to upload photo to Firebase storage, get URL to photo to add to post object
     */
    private fun uploadImageToFirebase(pathToUploadFile: Uri){
        if(photoUri != null){
            Log.d(TAG, "acceptable URI:" + photoUri.toString())
            val reference = storageRef?.child("postImgUploads/" + UUID.randomUUID().toString()) //Create reference to remote file
            val uploadImgTask = reference?.putFile(photoUri!!)
            val uploadUrlTask = uploadImgTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                Log.d(TAG, "Made it inside task")
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                Log.d(TAG, "Task didn't fail")
                return@Continuation reference.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uri = task.result //Get the URL
                    //Do something with the uri
                    uploadableFilePath = uri.toString() //Set the URL
                    Log.d(TAG,"got it")
                    postEvent.isEnabled = true //Enable post event now that the photo has uploaded sucessfully
                } else {
                    Log.d(TAG, "An error occured uploading a file")
                }
            }?.addOnFailureListener{
                Log.d(TAG, "Why it no work")
            }
            Log.d(TAG,"Nothing happened...")
        }else{
            Log.d(TAG, "You need to upload an image")
        }
    }

    /**
     * Handle the result from the photo app
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PHOTO){
            updatePhotoView()
            uploadImageToFirebase(photoUri)
        }
    }

    /**
     * Put the new photo into the photo view so the user can see what it will look like in the post
     */
    private fun updatePhotoView() {
        if(photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity()) //Scale the bitmap down
            val rotbitmap = orientBitmap(photoFile.path, bitmap) //Rotate the bitmap correctly
            eventImage.setImageBitmap(rotbitmap) //Set the ImageView

        } else {
            eventImage.setImageDrawable(null)
        }
    }


    companion object {
        fun newInstance(accountName: String, userName: String): FoodEventCreateFragment {
            val args = Bundle().apply {
                putSerializable(ARG_EMAIL, accountName)
                putSerializable(ARG_USERNAME, userName)
            }
            return FoodEventCreateFragment().apply {
                arguments = args
            }
        }
    }
}