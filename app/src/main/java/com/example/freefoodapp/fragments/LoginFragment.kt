package com.example.freefoodapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
import com.example.freefoodapp.R
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.globalUserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {
    private lateinit var loginImageView: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBUsers: DatabaseReference //Registering
    lateinit var DBAuth: FirebaseAuth //Registering
    private var accountName: String = ""
    private var password: String = ""
    private var username: String = ""

    /**
     * Interface to use callback functions defined in MainActivity
     */
    interface MainCallbacks {
        fun onLogin(accountName: String, userName: String)
        fun onClickRegister()
    }
    private var mainCallbacks: MainCallbacks? = null

    /**
     * Sets up the database
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBAuth = FirebaseAuth.getInstance() //registering
        DBUsers = DB.child(DatabaseVars.FIREBASE_USERS) //registering
    }

    /**
     * Enables use of callback functions from MainActivity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainCallbacks = context as MainCallbacks?
    }

    /**
     * initialize all of the UI Views and setup the button onClickListeners
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        emailTextView = view.findViewById(R.id.emailTextView) as TextView
        passwordTextView = view.findViewById(R.id.passwordTextView) as TextView
        loginImageView = view.findViewById(R.id.loginImageView) as ImageView
        loginButton = view.findViewById(R.id.loginButton) as Button
        registerButton = view.findViewById(R.id.registerButton) as Button
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress) as EditText
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword) as EditText
        registerButton.setOnClickListener {
            /**
             * Go to the registration fragment to register
             */
            mainCallbacks?.onClickRegister()
        }
        /**
         * When login button is pressed, check that the login info is correct, and if so, go to the list of Food Events
         */
        loginButton.setOnClickListener {
            login(accountName, password)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * Create Text Watchers to observe EditText UI elements and update corresponding variables
     */
    override fun onStart() {
        super.onStart()
        val emailTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                accountName = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        editTextTextEmailAddress.addTextChangedListener(emailTextWatcher)
        val passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                password = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        editTextTextPassword.addTextChangedListener(passwordTextWatcher)
    }
    /**
     * Use firebase to check the given login information, and if their is an account with the same email and password, log them
     * into the app, passing on the user's email and username
     */
    private fun login(givenEmail: String, givenPassword: String) {
        var loginEmail = givenEmail
        var loginPassword = givenPassword
        if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
            Log.d(TAG, "Trying to login.")
            DBAuth.signInWithEmailAndPassword(loginEmail!!, loginPassword!!).addOnCompleteListener(this.requireActivity()) { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "LOGIN SUCCESS!")
                    //Do something here
                    Log.d(TAG, "Logged in User ID: ${task.result?.user?.uid}")
                    var temp = DBUsers.child("${task.result?.user?.uid}")
                    /**
                     * Retrieve user's username and send them to the list of Food Events
                     */
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
     * Retrieve username and move to FoodListFragment
     */
    var userEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snap in dataSnapshot.children) {
                Log.d(TAG, "UserEventListener: $snap")
                if(snap.key.equals("username")) {
                    globalUserName = "${snap.getValue()}"
                    username = "${snap.getValue()}"
                    Log.d(TAG, "Username: $globalUserName")
                    mainCallbacks?.onLogin(accountName, username)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    /**
     * Create a LoginFragment
     */
    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

}