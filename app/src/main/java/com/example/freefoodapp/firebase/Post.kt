package com.example.freefoodapp.firebase

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

//class Post {
//
//    companion object Creater {
//        fun createPost(): Post = Post()
//    }
//
//    var id: String? = null
//    var date: Date? = null
//    var description: String? = null
//    var image: String? = null
//    var likes: Long? = null
//    var location: String? = null
//    var name: String? = null
//    var user: String? = null
//}

class Post(var id: String?,
           var date: Date?,
           var description: String?,
           var image: String?,
           var likes: Long,
           var location: String?,
           var name: String?,
           var user: String?) :Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDate(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeDate(date)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeLong(likes)
        parcel.writeString(name)
        parcel.writeString(user)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}
