package com.example.freefoodapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freefoodapp.firebase.Post
import java.util.*

class FoodListFragment: Fragment() {
    interface Callbacks {
        fun onEventSelected()
    }
    private var callbacks: Callbacks? = null
    private lateinit var foodRecyclerView: RecyclerView
    private var adapter: FoodEventAdapter? = FoodEventAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            callbacks?.onEventSelected()
        }
    }

    private inner class FoodEventAdapter(var posts: List<Post>) :
        RecyclerView.Adapter<FoodEventHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodEventHolder {
            val view = layoutInflater.inflate(R.layout.food_event_item, parent, false)
            return FoodEventHolder(view)
        }

        override fun onBindViewHolder(holder: FoodEventHolder, position: Int) {
            val bbgame = posts[position]
            holder.bind(bbgame)
        }

        override fun getItemCount(): Int {
            return posts.size
        }
    }

    companion object {
        fun newInstance(): FoodListFragment {
            return FoodListFragment()
        }
    }

}