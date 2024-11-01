package com.example.UNIVERSE.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callBack:(String?)-> Unit){
    var imageUrl:String? = null
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri).addOnSuccessListener {
        it.storage.downloadUrl.addOnSuccessListener {
            imageUrl = it.toString()
            callBack(imageUrl)
        }
    }
}

