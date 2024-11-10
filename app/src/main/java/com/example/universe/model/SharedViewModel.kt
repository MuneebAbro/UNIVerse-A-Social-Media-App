package com.example.universe.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModel : ViewModel() {
    // Profile image, user name, and email as LiveData
    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: MutableLiveData<String?> = _profileImageUrl

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    // New LiveData to store the user ID
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    // Method to set user profile data including userId
    fun setUserData(uid: String, name: String, email: String, profileImageUrl: String?) {
        _userId.value = uid // Set user ID
        _userName.value = name
        _userEmail.value = email
        _profileImageUrl.value = profileImageUrl
    }

    // Method to set individual profile fields (in case they are updated separately)
    fun setProfileImageUrl(url: String) {
        _profileImageUrl.value = url
    }

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun setUserEmail(email: String) {
        _userEmail.value = email
    }
}
