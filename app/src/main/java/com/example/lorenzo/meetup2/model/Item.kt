package com.example.lorenzo.meetup2.model

class Item(val id: String,val description: String,val name: String, val zip: String, val price: String, val seller: String, val lon:Double, val lat:Double, val imageUrl:String?){

    constructor():this(" DEFAULT", " DEFAULT", "DEFAULT ", "DEFAULT ", " DEFAULT", "DEFAULT", 0.0, 0.0, "DEFAULT"){

    }

}