package com.example.freefoodapp

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefoodapp.firebase.Comment
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.*
import java.util.*

private const val TAG = "CommentsFragment"
private const val ARG_USERNAME = "User_Username"
private const val ARG_POSTID = "Post_ID"

class CommentsFragment: Fragment() {
    private lateinit var eventName: TextView
    private lateinit var staticComment: TextView
    private lateinit var comment: Button
    private lateinit var userComment: EditText
    private lateinit var commentsRecyclerView: RecyclerView
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBComments: DatabaseReference
    private var username: String = ""
    private var originPost: String = ""
    private var adapter: CommentsFragment.CommentsAdapter? = CommentsAdapter(emptyList())
    private var comments: MutableList<Comment> = emptyList<Comment>().toMutableList()
    private var commentContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBComments = DB.child(DatabaseVars.FIREBASE_COMMENTS)
        DBComments.orderByKey().addChildEventListener(commentListener)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        originPost = arguments?.getSerializable(ARG_POSTID) as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.comment_list, container, false)
        commentsRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        eventName = view.findViewById(R.id.eventName) as TextView
        staticComment = view.findViewById(R.id.staticComment) as TextView
        userComment = view.findViewById(R.id.userComment) as EditText
        comment = view.findViewById(R.id.comment) as Button
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = adapter
        comment.setOnClickListener {
            createComment(commentContent, username, originPost)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
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

    private fun updateUI(comments : List<Comment>) {
        adapter = CommentsAdapter(comments)
        commentsRecyclerView.adapter = adapter
    }

    val commentListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded comments:" + dataSnapshot.key!!)
            addCommentToAList(dataSnapshot)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "commentListener error: ", error.toException())
        }
    }

    private fun addCommentToAList(dataSnapshot: DataSnapshot) {
        val comment = Comment.createComment()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        comment.id = dataSnapshot.key
        var dateMap = map.get("date") as HashMap<String, Any>
        var tempDate = Date()
        tempDate.time = (dateMap.get("time") as Long)
        comment.date = tempDate
        comment.postID = map.get("postID") as String?
        comment.content = map.get("content") as String?
        comment.user = map.get("user") as String?
        if (comment.postID == originPost) {
            comments.add(comment)
            //toDoItemList!!.add(todoItem);
            //Put the new post somewhere
            Log.d(TAG, "New comment added: ${comment.id}")
            updateUI(comments)
        }
        // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

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

        var d: Date = Date()

    }

    private inner class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var comment: Comment
        private val comDateTime : TextView = itemView.findViewById(R.id.comdatetime)
        private val commentdesc : TextView = itemView.findViewById(R.id.commentdesc)
        private val display_name : TextView = itemView.findViewById(R.id.display_Name)

        init {
        }

        fun bind(comment: Comment) {
            this.comment = comment
            comDateTime.text = comment.date.toString()
            commentdesc.text = comment.content
            display_name.text = comment.user
        }
    }

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

    companion object {
        fun newInstance(userName: String, postID: String): FoodEventFragment {
            val args = Bundle().apply {
                putSerializable(ARG_USERNAME, userName)
                putSerializable(ARG_POSTID, postID)
            }
            return FoodEventFragment().apply {
                arguments = args
            }
        }
    }

}