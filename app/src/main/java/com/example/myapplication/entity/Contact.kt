package com.example.myapplication.entity

import androidx.annotation.DrawableRes

data class Contact(
    val name: String? = null,
    val phoneNo: String? = null,
    @DrawableRes val image: Int? = null,
)

data class ListOfContact(
    var listOfContact: List<Contact>
)
