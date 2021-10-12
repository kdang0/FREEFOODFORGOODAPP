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

class FoodListFragment: Fragment() {
    interface MainCallbacks {
        fun onEventSelected(email: String, userName: String, post: Post)
        fun onCreateEvent(email: String, userName: String)
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
        setHasOptionsMenu(true)
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
        Log.d(TAG, "POSTS IN LIST: $posts")
    }

    val postListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded posts:" + dataSnapshot.key!!)
            addPostToAList(dataSnapshot)
            adapter!!.update(posts)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "POST HAS RECIEVED AN UPDATE: " + snapshot.key)
            for (post in posts) {
                if(post.id.equals(snapshot.key)) {
                    post.likes = (snapshot.getValue() as HashMap<String, Any>).get("likes") as Long
                }
            }
            adapter!!.update(posts)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
//            Log.d(TAG, "DELETE REQUEST RECIEVED: " + snapshot.key)
//            var count = 0
//            for (post in posts) {
//                if(post.id.equals(snapshot.key)) {
//                    post.likes = (snapshot.getValue() as HashMap<String, Any>).get("likes") as Long
//                }
//                count++
//            }
//            posts.removeAt(count)
//            adapter!!.update(posts)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "postListener error: ", error.toException())
        }
    }

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
        //toDoItemList!!.add(todoItem);
        //Put the new post somewhere
        Log.d(TAG, "New post added: $post.id}")
        updateUI(posts)
        // adapter.notifyDataSetChanged()
        //Do something here to update post view
    }

    fun deletePostByID(postID: String) {
        DBPosts.child(postID).removeValue()
    }

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

        fun bind(post: Post) {
            this.post = post
            eventDate.text = post.date.toString()
            eventName.text = post.name.toString()
            numOfLikes.text = post.likes.toString()
            Glide.with(eventImage)
                .load(post.image)
                .into(eventImage)
        }

        override fun onClick(p0: View?) {
            Log.d(TAG, "Username in list is: $username")
            callbacks?.onEventSelected(email, username, post)
        }

        override fun onLongClick(p0: View?): Boolean {
            return deletePost(post)
        }
    }

    private inner class FoodEventAdapter(var postsPassed: List<Post>) :
        RecyclerView.Adapter<FoodEventHolder>() {
        private var list: List<Post> = postsPassed
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodEventHolder {
            val view = layoutInflater.inflate(R.layout.food_event_item, parent, false)
            return FoodEventHolder(view)
        }

        override fun onBindViewHolder(holder: FoodEventHolder, position: Int) {
            val post = list[position]
            holder.bind(post)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun update(newPosts: List<Post>){
            list = newPosts
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater){
        super.onCreateOptionsMenu(menu,inflater)
        inflater.inflate(R.menu.food_event_list, menu)
    }

    override fun onOptionsItemSelected(item:MenuItem) : Boolean {
        return when (item.itemId){
            R.id.newFoodEvent -> {
                callbacks?.onCreateEvent(email,username)
                return true
           }
            else ->return super.onOptionsItemSelected(item)
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