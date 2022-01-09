package com.kunal.neptune.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kunal.neptune.ui.main.PreviewMediaAdapter
import com.kunal.neptune.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import android.annotation.SuppressLint
import com.google.firebase.database.ktx.database
import kotlin.collections.HashMap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class NewPostBottomSheetDialogFragment : RoundedBottomSheetDialogFragment() {

lateinit var addImage:LinearLayout
lateinit var addVideo:LinearLayout
lateinit var postButton:LinearLayout
lateinit var contentET:EditText
lateinit var rvMedia:RecyclerView
lateinit var mediaAdapter: PreviewMediaAdapter
private var isAdapterApplied = false
    lateinit var imageList:MutableList<Uri>
    lateinit var videoList: MutableList<Uri>
    lateinit var videoThumbnail:MutableList<Uri>
    lateinit var imageThumbnailUrls:MutableList<String>
    lateinit var videoThumbnailUrls:MutableList<String>
    private val storage = Firebase.storage
    private lateinit var database:FirebaseDatabase
    private lateinit var databaseRef:DatabaseReference
    var storageRef = storage.reference
    var post = ""
    var areImagesUploaded = false
    var areVideosUploaded = false
    var isPostCreated = false
    var isPostInstantiated = false
    var isContentAdded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database
        databaseRef = database.reference
        imageList = ArrayList()
        videoList = ArrayList()
        videoThumbnail = ArrayList()
        imageThumbnailUrls = ArrayList()
        videoThumbnailUrls = ArrayList()
        post = getPostId()
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val h = (height * 70)/100
        view.layoutParams.height = h

        rvMedia = view.findViewById(R.id.rvMedia)
        addImage = view.findViewById(R.id.imageUpload)
        addVideo = view.findViewById(R.id.videoUpload)
        contentET = view.findViewById(R.id.contentET)
        postButton = view.findViewById(R.id.postButton)

        if(!isReadStoragePermissionGranted() && !isWriteStoragePermissionGranted()){
            requestReadAndWriteStoragePermissions()
        }
        else if(!isReadStoragePermissionGranted()){
            requestReadStoragePermission()
        }
        else if(!isWriteStoragePermissionGranted()){
            requestWriteStoragePermission()
        }

        addImage.setOnClickListener {

            if (isWriteStoragePermissionGranted() && isReadStoragePermissionGranted()){
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 101)
            }else if(!isReadStoragePermissionGranted() && !isWriteStoragePermissionGranted()){
                requestReadAndWriteStoragePermissions()
            }
            else if(!isReadStoragePermissionGranted()){
                requestReadStoragePermission()
            }
            else if(!isWriteStoragePermissionGranted()){
                requestWriteStoragePermission()
            }
        }

        addVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, 100)
        }

        postButton.setOnClickListener {
            if (isContentAdded){
                uploadThumbnails()
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        Log.i("areImagesUploaded",areImagesUploaded.toString())
                        Log.i("areVideosUploaded",areVideosUploaded.toString())
                        Log.i("isPostCreated",isPostCreated.toString())

                        if (areImagesUploaded && areVideosUploaded && isPostCreated){
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }else{
                            if (areVideosUploaded && areVideosUploaded && !isPostInstantiated){
                                createPost()
                                isPostInstantiated = true
                            }
                            handler.postDelayed(this,2000)
                        }
                    }
                }, 1000)
            }


        }
    }

    private fun createPost() {

//        val ref = databaseRef.child("posts").child(post)
//
//        for(i in imageThumbnailUrls.indices){
//            ref.child("images").push().setValue(imageThumbnailUrls[i])
//        }
//        for(i in videoThumbnailUrls.indices){
//            ref.child("videos").push().setValue(videoThumbnailUrls[i])
//        }
//
//        val handler = Handler()
//        val runnable = Runnable {
//            isPostCreated = true
//        }
//        handler.postDelayed(runnable,8000)
    }

    private fun uploadThumbnails() {
        uploadImageThumbnails()
        uploadVideoThumbnails()
    }

    private fun uploadVideoThumbnails() {
        val videoRef = storageRef.child("videos").child(post)
        for (i in videoThumbnail.indices){
            val ref = videoRef.child(getPostId())
            ref.putFile(videoThumbnail[i]).continueWithTask{ task->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task->
                if (task.isSuccessful){
                    videoThumbnailUrls.add(task.result.toString())
                }
                if (i == (videoThumbnail.size-1)){
                    areImagesUploaded = true
                }
            }
        }
        if (videoThumbnail.isEmpty()){
            areVideosUploaded = true
        }
    }

    private fun uploadImageThumbnails() {
        val imagesRef= storageRef.child("images").child(post)
        for (i in imageList.indices){
            val ref = imagesRef.child(getPostId())
            ref.putFile(imageList[i]).continueWithTask{ task->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task->
                if (task.isSuccessful){
                    Log.i("url",task.result.toString())
                    imageThumbnailUrls.add(task.result.toString())
                }
                if (i == (imageList.size-1)){
                    areImagesUploaded = true
                }
            }
        }
        if (imageList.isEmpty()){
            areImagesUploaded = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100){
            if (data!=null){
                val selectedVideo: Uri? = data.data
                val mMMR = MediaMetadataRetriever()
                mMMR.setDataSource(context, selectedVideo)
                val bitmap = mMMR.frameAtTime
                val resultUri = bitmap?.let { getImageUri(context!!, it) }
                videoThumbnail.add(resultUri!!)
                videoList.add(selectedVideo!!)
                isContentAdded = true
                if (isAdapterApplied){
                    mediaAdapter.addImage(resultUri,"video")
                }else{
                    val map:HashMap<Uri,String> = HashMap()
                    map[resultUri] = "video"
                    mediaAdapter = PreviewMediaAdapter(mutableListOf(resultUri),map)
                    rvMedia.apply {
                        layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                        adapter = mediaAdapter
                    }
                    isAdapterApplied = true
                    rvMedia.visibility = View.VISIBLE
                }
            }
        }
        if (requestCode == 101){
            if (data!=null){
                val resultUri = data.data
                imageList.add(resultUri!!)
                isContentAdded = true
                if (isAdapterApplied){
                    mediaAdapter.addImage(resultUri,"image")
                }else{
                    val map:HashMap<Uri,String> = HashMap()
                    map[resultUri] = "image"
                    rvMedia.visibility = View.VISIBLE
                    mediaAdapter = PreviewMediaAdapter(mutableListOf(resultUri),map)
                    rvMedia.apply {
                        layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                        adapter = mediaAdapter
                    }
                    isAdapterApplied = true
                }
            }
        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                val resultUri = result.uri
//                if (isAdapterApplied){
//                    mediaAdapter.addImage(resultUri)
//                }else{
//                    rvMedia.visibility = View.VISIBLE
//                    mediaAdapter = PreviewMediaAdapter(mutableListOf(resultUri))
//                    rvMedia.apply {
//                        layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
//                        adapter = mediaAdapter
//                    }
//                    isAdapterApplied = true
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
//                Toast.makeText(context, error.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            10 -> {
                val perms:HashMap<String,Int> = HashMap()
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for(i in permissions.indices){
                        perms[permissions[i]] = grantResults[i]
                    }
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(context as Activity)
                    }else{ //else any one or both the permissions are not granted
                        when {
                            !isReadStoragePermissionGranted() && !isWriteStoragePermissionGranted() -> {
                                requestReadAndWriteStoragePermissions()
                            }
                            !isReadStoragePermissionGranted() -> {
                                requestReadStoragePermission()
                            }
                            !isWriteStoragePermissionGranted() -> {
                                requestWriteStoragePermission()
                            }
                        }
                    }
                }
            }
            18->{
                val perms:HashMap<String,Int> = HashMap()
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for(i in permissions.indices){
                        perms[permissions[i]] = grantResults[i]
                    }
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(context as Activity)
                    }else{ //else any one or both the permissions are not granted
                        requestReadStoragePermission()
                    }
                }

            }
            26->{
                val perms:HashMap<String,Int> = HashMap()
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for(i in permissions.indices){
                        perms[permissions[i]] = grantResults[i]
                    }
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(context as Activity)
                    }else{ //else any one or both the permissions are not granted
                        requestWriteStoragePermission()
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isWriteStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context?.applicationContext!!,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                Log.v("TAG", "Write Permission is revoked")
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context?.applicationContext!!,Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                Log.v("TAG", "Read Permission is revoked")
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    private fun requestReadAndWriteStoragePermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),
            10
        )
    }

    private fun requestReadStoragePermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            18
        )
    }

    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            26
        )
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun getPostId(): String {
        val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 10) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }
}