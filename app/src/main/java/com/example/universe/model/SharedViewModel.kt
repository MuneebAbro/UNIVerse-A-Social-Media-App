package com.example.universe.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // Profile image, user name, and email as LiveData
    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _userUsername = MutableLiveData<String?>()
    val userUsername: MutableLiveData<String?> get() = _userUsername

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    // Method to set user profile data including userId
    fun setUserData(uid: String, name: String, email: String, profileImageUrl: String?, username: String?) {
        _userId.value = uid // Set user ID
        _userName.value = name
        _userEmail.value = email
        _profileImageUrl.value = profileImageUrl
        _userUsername.value = username // Set username
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

    fun setUserUsername(username: String?) {
        _userUsername.value = username
    }

    fun updateUserData(name: String, username: String?, imageUrl: String?) {
        _userName.value = name
        _userUsername.value = username
        _profileImageUrl.value = imageUrl
    }
}
