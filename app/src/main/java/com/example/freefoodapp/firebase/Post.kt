package com.example.freefoodapp.firebase

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Post object. It is of type Parcelable so that it can be passed in as a bundle.
 */
class Post(var id: String?,
           var date: Date?,
           var description: String?,
           var image: String?,
           var likes: Long,
           var location: String?,
           var name: String?,
           var user: String?) :Parcelable {

    /**
     * Takes a parcel object to create
     */
    constructor(parcel: Parcel) : this(
        parcel.readString(), //UUID of Post
        parcel.readDate(), //Date of post
        parcel.readString(), //The body of the description of the FreeFood
        parcel.readString(), //The URL to the img in the Firebase bucket
        parcel.readLong(), //The count of Likes
        parcel.readString(), //The description of the location of food
        parcel.readString(), //The title of the Post
        parcel.readString() //the username of the poster of the post
    )

    /**
     * Function to make it parcable in the bundle
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeDate(date)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeLong(likes)
        parcel.writeString(name)
        parcel.writeString(user)
    }

    /**
     * Necessary function, just return zero if described correctly
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * What to do when it is being created
     */
    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel) //Creates a post object
        }

        /**
         * initalize the array to null
         */
        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * Helps to encode the Date object
 */
fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

/**
 * Helps to decode the Date object as Kotlin does not know how to convert it to a Long without it
 */
fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}
