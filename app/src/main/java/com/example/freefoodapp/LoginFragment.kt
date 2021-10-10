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
import java.util.*

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {
    private lateinit var loginImageView: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private var accountName: String = ""
    private var password: String = ""

    interface MainCallbacks {
        fun onLogin(accountName: String)
        fun onClickRegister()
    }
    private var mainCallbacks: MainCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        emailTextView = view.findViewById(R.id.emailTextView) as TextView
        passwordTextView = view.findViewById(R.id.passwordTextView) as TextView
        loginImageView = view.findViewById(R.id.loginImageView) as ImageView
        loginButton = view.findViewById(R.id.loginButton) as Button
        registerButton = view.findViewById(R.id.registerButton) as Button
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress) as EditText
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword) as EditText
        registerButton.setOnClickListener {
            mainCallbacks?.onClickRegister()
        }
        loginButton.setOnClickListener {
            //check to see if login data is correct
            Log.d(TAG, "Account Name is: $accountName")
            Log.d(TAG, "Password is: $password")
            mainCallbacks?.onLogin(accountName)
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

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

}