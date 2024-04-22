package net.ezra.ui.students


import android.content.ClipData.Item
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import net.ezra.R


@Composable
fun AddStudents(navController: NavHostController) {


       Box (
           modifier = Modifier
               .fillMaxSize()

       ){
           Image(
               painter = painterResource(id = R.drawable.white),
               contentDescription = "",
               modifier = Modifier
                   .fillMaxSize(),
               contentScale = ContentScale.Crop

           )
       }


    LazyColumn {
        item {


    Column(

        modifier = Modifier
            .padding(15.dp)
    ){
        Row (
            modifier = Modifier
                .background(Color.Green)
                .fillMaxSize()
        ){

        Text(text = "Register Student")
 Spacer(modifier = Modifier.width(102.dp))

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = ""
            )
        }

        var photoUri: Uri? by remember { mutableStateOf(null) }
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            photoUri = uri
        }

        var studentName by rememberSaveable {
            mutableStateOf("")
        }

        var studentClass by rememberSaveable {
            mutableStateOf("")
        }
        var studentAge by rememberSaveable {
            mutableStateOf("")
        }
        var studentGender by rememberSaveable {
            mutableStateOf("")
        }
        
        


        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text(text = "Name") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = studentClass,
            onValueChange = { studentClass= it },
            label = { Text(text = "Class") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = studentAge,
            onValueChange = { studentAge = it },
            label = { Text(text = "Age") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = studentGender,
            onValueChange = { studentGender= it },
            label = { Text(text = "Gender") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )






            OutlinedButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                        //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
                        //Or use .VideoOnly if you only want videos.
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                    )
                }
            ) {
                Text("Select Image")
            }


            if (photoUri != null) {
                //Use Coil to display the selected image
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = photoUri)
                        .build()
                )
                
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(150.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray),
                    contentScale = ContentScale.Crop,

                )
            }


        OutlinedButton(onClick = {
            photoUri?.let { uploadImageToFirebaseStorage(it, studentName, studentClass, studentAge, studentGender) }
            
        }) {
            
            Text(text = "Register")

            
        }








    }
        }


    }

}



fun uploadImageToFirebaseStorage(imageUri: Uri, studentName: String, studentClass: String, studentAge: String, studentGender: String) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("images/${UUID.randomUUID()}")

    val uploadTask = imageRef.putFile(imageUri)
    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            saveToFirestore(downloadUri.toString(), studentName, studentClass, studentAge, studentGender)
        } else {


        }
    }
}

fun saveToFirestore(imageUrl: String, studentName: String, studentClass: String, studentAge: String, studentGender: String) {
    val db = Firebase.firestore
    val imageInfo = hashMapOf(
        "imageUrl" to imageUrl,
        "studentName" to studentName,
        "studentClass" to studentClass,
        "studentAge" to studentAge,
        "studentGender" to studentGender



       
    )
    



    db.collection("Students")
        .add(imageInfo)
        .addOnSuccessListener {
          


        }
        .addOnFailureListener {
            // Handle error
        }
}







@Preview(showBackground = true)
@Composable
fun PreviewLight() {
    AddStudents(rememberNavController())
}





