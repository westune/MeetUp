package com.example.lorenzo.meetup2.model

class ListItem(name:String, price:Double, photoUrl:String){

    private var name:String? = null
    private var price:Double? = null
    private var photoUrl:String? = null

    init {
        this.name = name
        this.photoUrl = photoUrl
        this.price = price
    }


}

