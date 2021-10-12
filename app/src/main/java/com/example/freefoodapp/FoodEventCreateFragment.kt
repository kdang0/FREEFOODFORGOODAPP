package com.example.freefoodapp

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
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*

private const val TAG = "FoodEventCreateFragment"
private const val ARG_EMAIL = "User_Email"
private const val ARG_USERNAME = "User_Username"
private const val REQUEST_PHOTO = 2

class FoodEventCreateFragment: Fragment() {
    private lateinit var photoFile : File
    private lateinit var photoUri : Uri
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
    private var email: String = ""
    private var username: String = ""
    private var uploadableFilePath: String? = null
    private var storageRef: StorageReference? = null

    interface MainCallbacks {
        fun onPost(email: String, userName: String)
    }
    private var mainCallbacks: MainCallbacks? = null

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
            createPost(descrip, name, uploadableFilePath!!, loc, email)
            mainCallbacks?.onPost(email, username)
        }
        postEvent.isEnabled = false
//        uploadImage.setOnClickListener {
//            //image stuff
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoFile = File(context?.applicationContext?.filesDir, "IMG_EVENTPICTURE.jpg")
        photoUri =  FileProvider.getUriForFile(requireActivity(),
        "com.example.freefoodapp", photoFile)
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

        uploadImage.apply {
            val packageManager : PackageManager =
                requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY)
//            if (resolvedActivity == null){
//                isEnabled = false
//            }
            //Removed so emulator camera is enabled.

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
                    uploadImageToFirebase(photoUri)
                    startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

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
        if(photoUri != null){
            Log.d(TAG, "acceptable URI:" + photoUri.toString())
            val reference = storageRef?.child("postImgUploads/" + UUID.randomUUID().toString())
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
                    val uri = task.result
                    //Do something with the uri
                    uploadableFilePath = uri.toString()
                    Log.d(TAG,"got it")
                    postEvent.isEnabled = true
                } else {
                    Log.d(TAG, "An error occured uploading a file")
                }
            }?.addOnFailureListener{
                //Put something here for another failure?
                Log.d(TAG, "Why it no work")
            }
            Log.d(TAG,"Nothing happened...")
        }else{
            Log.d(TAG, "You need to upload an image")
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == REQUEST_PHOTO){
////            Log.d(TAG, photoUri.toString())
//            updatePhotoView()
//        }
//    }

//    private fun updatePhotoView() {
//        if(photoFile.exists()) {
//            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
//            val rotbitmap = orientBitmap(photoFile.path, bitmap)
//            eventImage.setImageBitmap(rotbitmap)
//
//        } else {
//            eventImage.setImageDrawable(null)
//        }
//    }


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