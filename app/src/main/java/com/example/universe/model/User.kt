package com.example.universe.model

class User(
        var image: String? = null,
        var name: String? = null,
        var email: String? = null,
        var password: String? = null,
        var username: String? = null,
        var gender: String? = null,
        var bio: String? = null,
        var dob: String? = null, // New field for date of birth
        var city: String? = null // New field for date of birth
) {
    // No additional constructors needed; use default values
}
