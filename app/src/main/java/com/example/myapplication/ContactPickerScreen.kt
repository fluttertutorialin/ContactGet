package com.example.myapplication

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.myapplication.entity.Contact
import com.example.myapplication.viewModel.ContactsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

var mapOfContacts = HashMap<Contact, Boolean>()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactPickerScreen(
    contactsViewModel: ContactsViewModel = hiltViewModel()
) {
    val contactsState = contactsViewModel.contactsState

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CONTACTS
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

//    hideErrorText()
    if (permissionState.allPermissionsGranted) {
        contactsViewModel.fetchContactList(LocalContext.current.contentResolver)
    }

    val selectedStates = remember {
        contactsState.contacts?.let { List(it.size) { index -> index to false }.toMutableStateMap() }
    }

    if (permissionState.allPermissionsGranted) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (contactsState.contacts?.size == 0) {
                Column(
                    modifier = Modifier.align(CenterHorizontally)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "You don't have any contacts yet ")
                }
            }
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items = contactsState.contacts ?: emptyList()) { index, contact ->
                    if (selectedStates != null) {
                        ListItemView(contact, selectedStates, index)
                    }
                }
            }
        }
    } else {
        permissionState.permissions.forEach { perm ->
            when (perm.permission) {
                Manifest.permission.READ_CONTACTS -> {
                    when {
                        perm.status.isGranted -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Permission Given YES ")
                            }
                        }
                        !perm.status.shouldShowRationale -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Permission was denied, Set it from Settings ")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemView(contact: Contact, selectedStates: SnapshotStateMap<Int, Boolean>, index: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .padding(vertical = 6.dp, horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = contact.name ?: "",
                    fontSize = if (selectedStates[index] == false) 16.sp else 12.sp
                )

                Text(
                    text = contact.phoneNo ?: "",
                    fontSize = if (selectedStates[index] == false) 16.sp else 12.sp
                )
            }

            Checkbox(
                checked = selectedStates[index] == false,
                onCheckedChange = {
                    selectedStates[index] = !it
                    mapOfContacts[contact] = it
                }
            )
        }
    }
}
