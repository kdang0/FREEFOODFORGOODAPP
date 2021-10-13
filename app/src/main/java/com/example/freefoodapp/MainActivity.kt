package com.example.freefoodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.freefoodapp.firebase.Post
import com.example.freefoodapp.fragments.*
import com.google.firebase.storage.StorageReference

private const val TAG = "MainActivity"

var globalUserName: String? = null
var storageRef: StorageReference? = null

class MainActivity : AppCompatActivity(), LoginFragment.MainCallbacks, RegistrationFragment.MainCallbacks, ConfirmationFragment.MainCallbacks,
    FoodListFragment.MainCallbacks, FoodEventCreateFragment.MainCallbacks, FoodEventFragment.MainCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = LoginFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                .commit()
        }
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


    override fun onGoToComments(userName: String, postID: String, postName: String) {
        Log.d(TAG, "Go to comments called.")
        val fragment = CommentsFragment.newInstance(userName, postID, postName)
        Log.d(TAG, "New comments created.")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }


    override fun onCreateEvent(email: String, userName: String) {
        val fragment = FoodEventCreateFragment.newInstance(email, userName)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}