package com.example.freefoodapp

import android.util.Log
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    @Test
    fun createPostObject() {
        var date = Date()
        var testPost = Post("TestID", date, "Test", "Test", 0, "Test", "Test", "Test")
        assertEquals("TestID", testPost.id)
    }

    @Test
    fun createCommentObject() {
        var date = Date()
        var testComment = Comment()
        testComment.user = "TestUser"
        testComment.content = "TestContent"
        testComment.postID = "TestPostID"
        testComment.date = date
        testComment.id = "TestID"
        assertEquals( "TestID",testComment.id)
        assertEquals(date, testComment.date)
    }

    @Test
    fun checkPassword() {
        var frag = RegistrationFragment()
        var password = "cheese"
        var wrongPassword = "chives"
        assertEquals(frag.verifyPassword(password, password), true)
        assertEquals(frag.verifyPassword(password, wrongPassword), false)
    }
}