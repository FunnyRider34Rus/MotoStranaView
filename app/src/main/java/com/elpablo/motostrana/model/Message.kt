package com.elpablo.motostrana.model

class Message {
    var uid: String? = null
    var text: String? = null
    var name: String? = null
    var date: String? = null
    var time: String? = null
    var photoUrl: String? = null
    var imageUrl: String? = null
    var reply: String? = null

    // Пустой конструктор для Firestore serialization
    constructor()

    constructor(uid: String?, text: String?, name: String?, date: String?, time: String?, photoUrl: String?, imageUrl: String?, reply: String?) {
        this.uid = uid
        this.text = text
        this.name = name
        this.date = date
        this.time = time
        this.photoUrl = photoUrl
        this.imageUrl = imageUrl
        this.reply = reply
    }
}
