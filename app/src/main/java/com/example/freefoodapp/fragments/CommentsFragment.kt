package com.example.freefoodapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.google.firebase.database.*
import java.util.*
import com.example.freefoodapp.R


private const val TAG = "CommentsFragment"
private const val ARG_USERNAME = "User_Username" //Username argument passed
private const val ARG_POSTID = "Post_ID" //PostID argument passed
private const val ARG_POSTNAME = "Post_Name" //PostName argument passed

class CommentsFragment: Fragment() {
    private lateinit var eventName: TextView //TextView to hold the name of the Post
    private lateinit var staticComment: TextView //TextView to hold comment
    private lateinit var comment: Button //Post comment button
    private lateinit var userComment: EditText //User input for comment
    private lateinit var commentsRecyclerView: RecyclerView //Recyclerview to hold all post comments
    lateinit var DB: DatabaseReference //Reference to Firebase Database
    lateinit var DBComments: DatabaseReference //Reference to Firebase Database child of Comments
    private var username: String = "" //Username of current user
    private var originPost: String = "" //The PostID for the comment
    private var adapter: CommentsAdapter? = CommentsAdapter(emptyList())
    private var comments: MutableList<Comment> = emptyList<Comment>().toMutableList() //List of all comments for this post
    private var commentContent: String = "" //The comment to be posted
    private var postName: String = "" //The name of the current post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //Initalize the DB connection
        DBComments = DB.child(DatabaseVars.FIREBASE_COMMENTS) //Initalize the DB connection for comments
        DBComments.orderByKey().addChildEventListener(commentListener) //Start the childevent listener for new comments
        username = arguments?.getSerializable(ARG_USERNAME) as String //get the username passe d
        originPost = arguments?.getSerializable(ARG_POSTID) as String //get the post passed
        postName = arguments?.getSerializable(ARG_POSTNAME) as String //get the post's title passed
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.comment_list, container, false)
        /**
         * Reference all the view's UI elements
         */
        commentsRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        eventName = view.findViewById(R.id.eventName) as TextView
        staticComment = view.findViewById(R.id.staticComment) as TextView
        userComment = view.findViewById(R.id.userComment) as EditText
        comment = view.findViewById(R.id.comment) as Button
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = adapter
        eventName.setText(postName)
        comment.setOnClickListener {

            //Dismiss keyboard and erase text field when it is posted
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            createComment(commentContent, username, originPost)
            userComment.setText("") //reset text in EditText to indicate post was sucessful
            Toast.makeText(activity, "Comment Posted", Toast.LENGTH_SHORT).show() //Not currently working
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        /**
         * As text is entered into the EditText do something with it
         */
        val contentTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //space left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                commentContent = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //also blank
            }
        }
        userComment.addTextChangedListener(contentTextWatcher)
    }

    /**
     * This will allow the UI to update when it is given a list of comments,
     * and sets the adapter so we can use the internal update afterwards
     */
    private fun updateUI(comments : List<Comment>) {
        adapter = CommentsAdapter(comments)
        commentsRecyclerView.adapter = adapter
    }

    /**
     * This waits for comments to be added, edited, or deleted and does the correct funtion when it happens
     */
    val commentListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded comments:" + dataSnapshot.key!!)
            addCommentToAList(dataSnapshot) //Send the new comment to the function to handle
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "commentListener error: ", error.toException())
        }
    }

    /**
     * Function to decode comment from Firebase Database
     */
    private fun addCommentToAList(dataSnapshot: DataSnapshot) {
        val comment = Comment.createComment()
        val map = dataSnapshot.getValue() as HashMap<String, Any> //Contains all the values of the comment
        comment.id = dataSnapshot.key //This is the UUID of the comment
        var dateMap = map.get("date") as HashMap<String, Any>
        var tempDate = Date()
        tempDate.time = (dateMap.get("time") as Long)
        comment.date = tempDate
        comment.postID = map.get("postID") as String?
        comment.content = map.get("content") as String?
        comment.user = map.get("user") as String?

        /**
         * This checks if the comment is for this post. If it is, we want to add it to the recycleview. Else do nothing
         */
        if (isOfPost(comment, originPost)) {

            comments.add(comment)
            //toDoItemList!!.add(todoItem);
            //Put the new post somewhere
            Log.d(TAG, "New comment added: ${comment.id}")
            updateUI(comments)
        }
        // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    /**
     * This function creats a comment and populates it with the right data
     */
    fun createComment(content: String, user: String, postID: String) {
        val comment = Comment.createComment()
        comment.date = Date()
        comment.content = content
        comment.user = user
        comment.postID = postID

        val newComment = DB.child(DatabaseVars.FIREBASE_COMMENTS).push()
        comment.id = newComment.key
        newComment.setValue(comment)
        Log.d(TAG, "Comment uploaded to cloud")
    }

    /**
     * Function to check if the Post ID of the comment matches the current Post
     */
    fun isOfPost(comment: Comment, originPost: String): Boolean {
        return (comment.postID == originPost)
    }

    /**
     * This handles the individual comment fragments
     */
    private inner class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var comment: Comment
        private val comDateTime : TextView = itemView.findViewById(R.id.comdatetime)
        private val commentdesc : TextView = itemView.findViewById(R.id.commentdesc)
        private val display_name : TextView = itemView.findViewById(R.id.display_Name)

        init {
        }

        /**
         * Here we set each fragment's UI elements to the Comment's data
         */
        fun bind(comment: Comment) {
            this.comment = comment
            comDateTime.text = comment.date.toString()
            commentdesc.text = comment.content
            display_name.text = comment.user
        }
    }

    /**
     * This is the adapter that handles the comments
     */
    private inner class CommentsAdapter(var comments: List<Comment>) :
        RecyclerView.Adapter<CommentHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
            val view = layoutInflater.inflate(R.layout.comment_item, parent, false)
            return CommentHolder(view)
        }

        override fun onBindViewHolder(holder: CommentHolder, position: Int) {
            val comment = comments[position]
            holder.bind(comment)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }

    /**
     * This is for passing data
     */
    companion object {
        fun newInstance(userName: String, postID: String, postName: String): CommentsFragment {
            val args = Bundle().apply {
                putSerializable(ARG_USERNAME, userName)
                putSerializable(ARG_POSTID, postID)
                putSerializable(ARG_POSTNAME, postName)
            }
            return CommentsFragment().apply {
                arguments = args
            }
        }
    }

}