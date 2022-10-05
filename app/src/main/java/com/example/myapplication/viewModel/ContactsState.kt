package com.example.myapplication.viewModel

import com.example.myapplication.entity.Contact

data class ContactsState(
    var contacts : List<Contact>? = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)
