package com.example.lorenzo.meetup2.model

class ChatMessage(text: String, name: String, photoUrl: String){
    private var text:String? = null
    private var name:String? = null
    private var photoUrl:String? = null

    init{
        this.text = text
        this.name = name
        this.photoUrl = photoUrl
    }

    fun getText():String{
        return this.text!!
    }

    fun getName():String{
        return this.text!!
    }

    fun getPhotoUrl():String{
        return this.text!!
    }

}