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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "RegistrationFragment"

class RegistrationFragment: Fragment() {
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var confirmTextView: TextView
    private lateinit var signupButton: Button
    private lateinit var emailaddressEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBUsers: DatabaseReference //Registering
    lateinit var DBAuth: FirebaseAuth //Registering
    private var emailAddress: String = ""
    private var username: String = ""
    private var password: String = ""
    private var confirmedPassword: String = ""

    interface MainCallbacks {
        fun onRegister()
    }
    private var mainCallbacks: MainCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBAuth = FirebaseAuth.getInstance() //registering
        DBUsers = DB.child("Users") //registering
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainCallbacks = context as RegistrationFragment.MainCallbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_page, container, false)
        emailTextView = view.findViewById(R.id.emailTextView) as TextView
        usernameTextView = view.findViewById(R.id.usernameTextView) as TextView
        passwordTextView = view.findViewById(R.id.passwordTextView) as TextView
        confirmTextView = view.findViewById(R.id.confirmTextView) as TextView
        signupButton = view.findViewById(R.id.signup) as Button
        emailaddressEditText = view.findViewById(R.id.emailaddress) as EditText
        usernameEditText = view.findViewById(R.id.username) as EditText
        passwordEditText = view.findViewById(R.id.password) as EditText
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword) as EditText
        signupButton.setOnClickListener {
            var samePassword: Boolean = verifyPassword(password, confirmedPassword)
            if (samePassword) {
                createNewAccount()
            }
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
                emailAddress = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        emailaddressEditText.addTextChangedListener(emailTextWatcher)
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
        passwordEditText.addTextChangedListener(passwordTextWatcher)
        val usernameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                username = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        usernameEditText.addTextChangedListener(usernameTextWatcher)
        val confirmTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                confirmedPassword = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        confirmPasswordEditText.addTextChangedListener(confirmTextWatcher)
    }

    fun verifyPassword(password: String, confirmedPassword: String): Boolean {
        var samePassword: Boolean = (password == confirmedPassword)
        return samePassword
    }

    private fun createAccount(): Boolean {
        var isCreated: Boolean = true
        //Check that there's no duplicate password
        //check that there's no duplicate username
        //check that password and confirm password are the same
        //create account
        return isCreated
    }

    private fun createNewAccount() {
        Log.d(TAG, "Creating new account...")
        var username = this.username
        var email = this.emailAddress
        var password = this.password

        if(!username!!.isEmpty() && !email!!.isEmpty() && !password!!.isEmpty()) {
            DBAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Creating user worked!")
                        val userId = DBAuth!!.currentUser!!.uid
                        val currentUserDb = DBUsers!!.child(userId)
                        currentUserDb.child("username").setValue(username)
                        mainCallbacks?.onRegister()
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

    companion object {
        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }
}