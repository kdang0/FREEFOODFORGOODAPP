package com.example.freefoodapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.*
import java.util.*

private const val TAG = "FoodListFragment"
private const val ARG_EMAIL = "User_Email"
private const val ARG_USERNAME = "User_Username"

class FoodListFragment: Fragment() {
    interface MainCallbacks {
        fun onEventSelected(email: String, userName: String, post: Post)
    }
    private var callbacks: MainCallbacks? = null
    private lateinit var foodRecyclerView: RecyclerView
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBPosts: DatabaseReference
    private var adapter: FoodEventAdapter? = FoodEventAdapter(emptyList())
    private var posts: MutableList<Post> = emptyList<Post>().toMutableList()
    private lateinit var username: String
    private lateinit var email: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as MainCallbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        DBPosts.orderByKey().addChildEventListener(postListener)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        email = arguments?.getSerializable(ARG_EMAIL) as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.food_event_list, container, false)
        foodRecyclerView = view.findViewById(R.id.foodRecyclerView) as RecyclerView
        foodRecyclerView.layoutManager = LinearLayoutManager(context)
        foodRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(posts : List<Post>) {
        adapter = FoodEventAdapter(posts)
        foodRecyclerView.adapter = adapter
    }

    val postListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded posts:" + dataSnapshot.key!!)
            addPostToAList(dataSnapshot)
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
            Log.w(TAG, "postListener error: ", error.toException())
        }
    }

    private fun addPostToAList(dataSnapshot: DataSnapshot) {
        val post = Post.createPost()
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        Log.d(TAG, "map contans: $map")

        post.id = dataSnapshot.key
        post.user = map.get("user") as String?
        var dateMap = map.get("date") as HashMap<String, Any>

        var tempDate = Date()

        tempDate.time = (dateMap.get("time") as Long)
        post.date = tempDate
        Log.d(TAG, "dateMap: $dateMap")
        Log.d(TAG, "createdDate: $tempDate")

        post.location = map.get("location") as String?
        post.likes = map.get("likes") as Long?
        post.image = map.get("image") as String?
        post.name = map.get("name") as String?
        post.description = map.get("description") as String?
        posts.add(post)
        //toDoItemList!!.add(todoItem);
        //Put the new post somewhere
        Log.d(TAG, "New post added: $post.id}")
        updateUI(posts)
        // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    private inner class FoodEventHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var post: Post
        private val likesImageView : ImageView = itemView.findViewById(R.id.likesImageView)
        private val eventImage : ImageView = itemView.findViewById(R.id.eventImage)
        private val eventDate : TextView = itemView.findViewById(R.id.eventDate)
        private val eventName : TextView = itemView.findViewById(R.id.eventName)
        private val numOfLikes : TextView = itemView.findViewById(R.id.numOfLikes)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(post: Post) {
            this.post = post
            eventDate.text = post.date.toString()
            eventName.text = post.name.toString()
            numOfLikes.text = post.likes.toString()
        }

        override fun onClick(p0: View?) {
            callbacks?.onEventSelected(email, username, post)
        }
    }

    private inner class FoodEventAdapter(var posts: List<Post>) :
        RecyclerView.Adapter<FoodEventHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodEventHolder {
            val view = layoutInflater.inflate(R.layout.food_event_item, parent, false)
            return FoodEventHolder(view)
        }

        override fun onBindViewHolder(holder: FoodEventHolder, position: Int) {
            val post = posts[position]
            holder.bind(post)
        }

        override fun getItemCount(): Int {
            return posts.size
        }
    }

    companion object {
        fun newInstance(accountName: String, userName: String): FoodListFragment {
            val args = Bundle().apply {
                putSerializable(ARG_EMAIL, accountName)
                putSerializable(ARG_USERNAME, userName)
            }
            return FoodListFragment().apply {
                arguments = args
            }
        }
    }

}