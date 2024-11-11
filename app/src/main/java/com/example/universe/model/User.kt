package com.example.universe.model

class User {
    var image: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var username: String? = null // New field for username

    constructor() // No-arg constructor needed for Firebase deserialization

    constructor(image: String?, name: String?, email: String?, password: String?, username: String?) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
        this.username = username
    }

    constructor(name: String?, email: String?, password: String?, username: String?) {
        this.name = name
        this.email = email
        this.password = password
        this.username = username
    }

    constructor(email: String?, password: String?, username: String?) {
        this.email = email
        this.password = password
        this.username = username
    }
}
