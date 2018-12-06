package com.example.lorenzo.meetup2.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import com.example.lorenzo.meetup2.model.Item
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class PostItemFragment : Fragment() {

    private val LOG = "Post Item Fragment"
    private val LAYOUT = R.layout.post_item_fragment
    private lateinit var priceText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var nameText: EditText
    private lateinit var zipText: EditText
    private lateinit var postButton: Button
    private lateinit var locationButton: AppCompatImageButton
    private lateinit var imageButton: ImageButton
    private val REQUEST_PICK_IMAGE = 1
    private var imageUri: Uri? = null
    private lateinit var mStorageRef:StorageReference
    private lateinit var mActivity:MainActivity


    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG, "On Create")
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mActivity = activity as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)
        priceText = view!!.findViewById(R.id.price)
        nameText = view.findViewById(R.id.name)
        descriptionText = view.findViewById(R.id.description)
        zipText = view.findViewById(R.id.zip)
        postButton = view.findViewById(R.id.postButton)
        locationButton = view.findViewById(R.id.locationButton)
        imageButton = view.findViewById(R.id.imageButton)
        setUpButtons()
        return view
    }



    private fun setUpButtons(){
        postButton.setBackgroundColor(resources.getColor(R.color.red))
        postButton.setText(R.string.post_button)
        postButton.setTextColor(resources.getColor(R.color.white))
        postButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.postButton -> {
                    uploadFile()
                }
            }
        }

        locationButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.locationButton -> setZip()
            }
        }

        imageButton.setOnClickListener { ImageButton ->
            when (ImageButton.id){
                R.id.imageButton -> addImage()
            }
        }

    }

    private fun addImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onStart() {
        Log.d(LOG, "On Start")
        super.onStart()
    }

    override fun onResume() {
        Log.d(LOG, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(LOG, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(LOG, "On Stop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(LOG, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(LOG, "On Detach")
        super.onDetach()
    }

    override fun onDestroyView() {
        Log.d(LOG, "On Destroy View")
        super.onDestroyView()
    }

    private fun checkForEmptyInputs(name: String, description: String, price: String, zip: String): Boolean {
        when {
            name.isEmpty() -> nameText.error = "Please Enter a Name"
            description.isEmpty() -> descriptionText.error = "Please Enter a Description"
            price.isEmpty() -> priceText.error = "Please Enter a Price"
            zip.isEmpty() -> zipText.error = "Please Enter a zip"
            else -> return false
        }
        return true

    }

    private fun postItem(name: String, description: String, price: String, zip: String, imageUrl:String) {
        if (checkForEmptyInputs(name, description, price, zip)) {
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        val id = ref.push().key
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        var long = 0.0
        var lat = 0.0
        val location = mActivity.sLocation
        when{
            location != null ->{
                long = location.longitude
                lat = location.latitude
            }
            location == null -> {
                val address = mActivity.getAddress(zipText.toString())
                if(address == null){
                    zipText.error = "Invalid Zip Code"
                    zipText.requestFocus()
                    return
                }else{
                    long = address.longitude
                    lat = address.latitude
                }
            }
        }
        val newItem = Item(id!!, description, name, zip, price, user, long, lat, imageUrl)
        ref.child(id).setValue(newItem)
        Toast.makeText(activity, "New Item Posted!", Toast.LENGTH_LONG).show()
        showPostItems()
    }


    private fun showPostItems() {
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = ItemsForSaleFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @SuppressLint("MissingPermission")
    private fun setZip() {
        zipText.setText(mActivity.getZip())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null){
            imageUri = data.data
            imageButton.setImageURI(imageUri)
            imageButton.background = null
        }else{
            Log.e(LOG, "Cannot get image for uploading")
        }
    }

    private fun getFileExtension(uri:Uri): String{
        val cR: ContentResolver = this.activity!!.contentResolver
        val mime:MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))!!
    }

    private fun uploadFile(){
        if(imageUri != null){
            val fileReference:StorageReference = mStorageRef.child("" +
                    System.currentTimeMillis() + "."
                + getFileExtension(imageUri!!))
            val uploadTask = fileReference.putFile(imageUri!!).addOnSuccessListener {
                //progress bar back to 0
            }.addOnFailureListener{
                Toast.makeText(this.activity!!, it.message, Toast.LENGTH_LONG).show()
            }

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    postItem(
                        nameText.text.toString().trim(),
                        descriptionText.text.toString().trim(),
                        priceText.text.toString().trim(),
                        zipText.text.toString().trim(),
                        downloadUri)
                } else {
                    // Handle failures
                    // ...
                }
            }

        } else {
            postItem(
                nameText.text.toString().trim(),
                descriptionText.text.toString().trim(),
                priceText.text.toString().trim(),
                zipText.text.toString().trim(),
                "")
        }
    }
}