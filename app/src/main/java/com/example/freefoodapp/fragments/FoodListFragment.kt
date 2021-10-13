package com.example.freefoodapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freefoodapp.R
import com.example.freefoodapp.firebase.DatabaseVars
import com.example.freefoodapp.firebase.Post
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "FoodListFragment"
private const val ARG_EMAIL = "User_Email"
private const val ARG_USERNAME = "User_Username"
//private const val ARG_POST = "Post"
/**
 * Callbacks Interface to connect to MainActivity
 */
class FoodListFragment: Fragment() {
    interface MainCallbacks {
        fun onEventSelected(email: String, userName: String, post: Post)
        fun onCreateEvent(email: String, userName: String)
    }
    private var callbacks: MainCallbacks? = null

    /**
     * lateinit recycler view and database variables
     */
    private lateinit var foodRecyclerView: RecyclerView
    lateinit var DB: DatabaseReference //Registering
    lateinit var DBPosts: DatabaseReference
    /**
     * Initialize inner class Adapter and class variables like list of posts and the received username and email of user
     */
    private var adapter: FoodEventAdapter? = FoodEventAdapter(emptyList())
    private var posts: MutableList<Post> = emptyList<Post>().toMutableList()
    private lateinit var username: String
    private lateinit var email: String

    /**
     * Enable use of callback functions from MainActivity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as MainCallbacks?
    }

    /**
     * Initialize database, retrieve variables passed in on creation of fragment, create menu bar
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB = FirebaseDatabase.getInstance().reference //registering
        DBPosts = DB.child(DatabaseVars.FIREBASE_POSTS)
        DBPosts.orderByKey().addChildEventListener(postListener)
        username = arguments?.getSerializable(ARG_USERNAME) as String
        email = arguments?.getSerializable(ARG_EMAIL) as String
        setHasOptionsMenu(true)
    }

    /**
     * Initialize RecyclerView and add it to layout manager and give it an adapter
     */
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
    /**
     * Update the UI to see changes to the list of posts occur IN REAL TIME
     */
    private fun updateUI(posts : List<Post>) {
        adapter = FoodEventAdapter(posts)
        foodRecyclerView.adapter = adapter
        Log.d(TAG, "POSTS IN LIST: $posts")
    }
    /**
     * Create object that observes the changes in the FireBase DataBase for posts
     */
    val postListener = object : ChildEventListener {
        /**
         * When post is added to database, add post to list of posts in fragment and update fragment UI
         */
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded posts:" + dataSnapshot.key!!)
            addPostToAList(dataSnapshot)
            adapter!!.update(posts)
        }

        /**
         * When post is changed, update it in fragments, and change likes shown if likes got changed
         */
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "POST HAS RECIEVED AN UPDATE: " + snapshot.key)
            for (post in posts) {
                if(post.id.equals(snapshot.key)) {
                    post.likes = (snapshot.getValue() as HashMap<String, Any>).get("likes") as Long
                }
            }
            adapter!!.update(posts)
        }

        /**
         * Function Not Used
         */
        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        /**
         * Function Not used
         */
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        /**
         * Print an error if an error occurs
         */
        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "postListener error: ", error.toException())
        }
    }

    /**
     * Retrieve a post from the database and put it in the fragment and update the UI
     */
    private fun addPostToAList(dataSnapshot: DataSnapshot) {
        val map = dataSnapshot.getValue() as HashMap<String, Any>
        Log.d(TAG, "map contans: $map")

        var id = dataSnapshot.key
        var user = map.get("user") as String?
        var dateMap = map.get("date") as HashMap<String, Any>

        var tempDate = Date()

        var time = (dateMap.get("time") as Long)
        var date = tempDate
        Log.d(TAG, "dateMap: $dateMap")
        Log.d(TAG, "createdDate: $tempDate")

        var location = map.get("location") as String?
        var likes: Long = map.get("likes") as Long
        var image = map.get("image") as String?
        var name = map.get("name") as String?
        var description = map.get("description") as String?
        val post = Post(id,date,description,image,likes,location,name,user)
        posts.add(post)
        Log.d(TAG, "New post added: $post.id}")
        updateUI(posts)
    }

    /**
     * Delete a post in the database
     */
    fun deletePostByID(postID: String) {
        DBPosts.child(postID).removeValue()
    }

    /**
     * If post is created by logged in User, Delete post from database and list of posts in fragment and update UI
     */
    fun deletePost(post: Post): Boolean {
        if(post.user == email) {
            post.id?.let { deletePostByID(it) }
            posts.remove(post)
            updateUI(posts)
            return true
        }
        else {
            return false
        }
    }

    /**
     * Inner class to hold UI for list elements shown in Recycler View
     */
    private inner class FoodEventHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        private lateinit var post: Post
        private val likesImageView : ImageView = itemView.findViewById(R.id.likesImageView)
        private val eventImage : ImageView = itemView.findViewById(R.id.eventImage)
        private val eventDate : TextView = itemView.findViewById(R.id.eventDate)
        private val eventName : TextView = itemView.findViewById(R.id.eventName)
        private val numOfLikes : TextView = itemView.findViewById(R.id.numOfLikes)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        /**
         * Put post's information inside Food_Event_Item
         */
        fun bind(post: Post) {
            this.post = post
            eventDate.text = post.date.toString()
            eventName.text = post.name.toString()
            numOfLikes.text = post.likes.toString()
            Glide.with(eventImage)
                .load(post.image)
                .into(eventImage)
        }

        /**
         * When Clicking a food event item, go to the detailed view of the food event item
         */
        override fun onClick(p0: View?) {
            Log.d(TAG, "Username in list is: $username")
            callbacks?.onEventSelected(email, username, post)
        }

        /**
         * Upon long clicking an event, delete it if the user is the one who created the event
         */
        override fun onLongClick(p0: View?): Boolean {
            return deletePost(post)
        }
    }

    /**
     * Inner class to create food events for each post
     */
    private inner class FoodEventAdapter(var postsPassed: List<Post>) :
        RecyclerView.Adapter<FoodEventHolder>() {
        private var list: List<Post> = postsPassed
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodEventHolder {
            val view = layoutInflater.inflate(R.layout.food_event_item, parent, false)
            return FoodEventHolder(view)
        }

        /**
         * Bind post information to their respective food events.
         */
        override fun onBindViewHolder(holder: FoodEventHolder, position: Int) {
            val post = list[position]
            holder.bind(post)
        }

        /**
         * Get number of posts
         */
        override fun getItemCount(): Int {
            return list.size
        }

        /**
         * Update RecyclerView
         */
        fun update(newPosts: List<Post>){
            list = newPosts
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Create menu bar at the top of the screen
     */
    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater){
        super.onCreateOptionsMenu(menu,inflater)
        inflater.inflate(R.menu.food_event_list, menu)
    }

    /**
     * Go to the fragment for creating a new post
     */
    override fun onOptionsItemSelected(item:MenuItem) : Boolean {
        return when (item.itemId){
            R.id.newFoodEvent -> {
                callbacks?.onCreateEvent(email,username)
                return true
           }
            else ->return super.onOptionsItemSelected(item)
       }
    }

    /**
     * Create a Food List Fragment with a given email and username
     */
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