package com.example.adminuser.models

class Upload {
    var imageUrl: String? = null
    var name: String? = null
    var longitude: String? = null
    var latitude: String? = null

    constructor(name: String?, imageUrl: String?, longitude: String?, latitude: String?) {
        this.imageUrl = imageUrl
        this.name = name
        this.longitude = longitude
        this.latitude = latitude
    }

    constructor() {}
}
