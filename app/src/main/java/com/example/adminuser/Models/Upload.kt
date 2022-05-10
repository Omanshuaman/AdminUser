package com.example.adminuser.Models

class Upload {
    var imageUrl: String? = null
    var name: String? = null

    constructor(name: String?,imageUrl: String?, ) {
        this.imageUrl = imageUrl
        this.name = name
    }

    constructor() {}
}
