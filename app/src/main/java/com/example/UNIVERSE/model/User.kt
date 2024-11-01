package com.example.UNIVERSE.model

class User {
    var image: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null

    constructor() // No-arg constructor needed for Firebase deserialization

    constructor(image: String?, name: String?, email: String?, password: String?) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(name: String?, email: String?, password: String?) {
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }
}
