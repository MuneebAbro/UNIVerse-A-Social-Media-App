package com.example.universe.model
import android.os.Parcel
import android.os.Parcelable

data class Post(
        val postUrl: String? = null,
        val caption: String? = null,
        val userEmail: String? = null,       // Email of the user who posted
        val userName: String? = null,        // Name of the user who posted
        val timestamp: Long = 0L,
        val userProfilePicture: String? = null, // URL of the user's profile picture
        val userId: String? = null           // User ID of the user who posted
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString() // Read userId from parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postUrl)
        parcel.writeString(caption)
        parcel.writeString(userEmail)
        parcel.writeString(userName)
        parcel.writeLong(timestamp)
        parcel.writeString(userProfilePicture)
        parcel.writeString(userId) // Write userId to parcel
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
