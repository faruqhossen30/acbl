package com.shopnolive.shopnolive.ui.user

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.api.client.RetrofitClientFactory.Companion.BASE_URL
import com.shopnolive.shopnolive.databinding.ActivityEditProfileBinding
import com.shopnolive.shopnolive.utils.*
import com.shopnolive.shopnolive.utils.Constants.REQUEST_CODE_SELECT_IMAGE
import com.shopnolive.shopnolive.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import com.shopnolive.shopnolive.utils.Constants.STORAGE_PERMISSIONS
import com.shopnolive.shopnolive.utils.Tools.hasPermissions
import com.shopnolive.shopnolive.utils.Variable.userInfo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var pickedImageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private lateinit var part: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.edNameProfile.setText(userInfo.getName())
        binding.edPhoneProfile.setText(userInfo.getPhone())
        if (!userInfo.getImage().isNullOrEmpty())
            binding.profilePictureProfile.loadImageFromUrl(BASE_URL + userInfo.getImage())

        binding.btnImageUploadProfile.setOnClickListener {
            binding.btnImageUploadProfile.isEnabled = false
            if (hasPermissions(this, STORAGE_PERMISSIONS)) {
                openGallery()
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE_PERMISSION)
                }
            }
        }

        binding.btnNameUpdate.setOnClickListener {
            binding.btnNameUpdate.isEnabled = false
            val name = binding.edNameProfile.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                binding.edNameProfile.error = "Enter your name"
                binding.edNameProfile.requestFocus()
                binding.btnNameUpdate.isEnabled = true
            } else {
                callApi(getRestApis().nameChange(userInfo.getId(), name),
                    onApiSuccess = {
                        toast("Name changed successfully")
                        binding.btnNameUpdate.isEnabled = true
                        finish()
                    },
                    onApiError = {
                        toast(it)
                        binding.btnNameUpdate.isEnabled = true
                    },
                    onNetworkError = {
                        toast("Network error")
                        binding.btnNameUpdate.isEnabled = true
                    }
                )
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openGallery()
            else {
                toast(resources.getString(R.string.permission_denied))
                binding.btnImageUploadProfile.isEnabled = true
            }
        } else {
            binding.btnImageUploadProfile.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                pickedImageUri = data.data
                if (pickedImageUri != null) {
                    if (pickedImageUri != null) {
                        bitmap =
                            ImageUtils.getInstant()
                                .getCompressedBitmap(getRealPathFromURI(pickedImageUri!!), 60)

                        binding.profilePictureProfile.setImageBitmap(bitmap)

                        uploadImageIntoStorage()
                    } else {
                        binding.btnImageUploadProfile.isEnabled = true
                    }
                }
            }
        }
    }

    private fun uploadImageIntoStorage() {
        binding.progressBarImageUpload.visibility = View.VISIBLE

        val tempUri: Uri = getImageUri(applicationContext, bitmap!!)

        val finalFile = File(getRealPathFromURI(tempUri))

        val requestFile = RequestBody.create(
            MediaType.parse(contentResolver.getType(tempUri)!!),
            finalFile
        )

        part = MultipartBody.Part.createFormData(
            "image",
            finalFile.name,
            requestFile
        )

        val id: RequestBody = RequestBody.create(MediaType.parse("text/plain"), userInfo.getId())

        callApi(getRestApis().imageUploadFile(id, part),
            onApiSuccess = {
                toast("Image Uploaded successfully")
                binding.progressBarImageUpload.visibility = View.GONE
                binding.btnImageUploadProfile.isEnabled = true
            },
            onApiError = {
                //toast(it)
                binding.progressBarImageUpload.visibility = View.GONE
                binding.btnImageUploadProfile.isEnabled = true
            },
            onNetworkError = {
                toast("No Internet Connection")
                binding.btnImageUploadProfile.isEnabled = true
            }
        )

    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val cursor: Cursor = contentResolver.query(contentUri, null, null, null, null)!!
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                System.currentTimeMillis().toString(),
                null
            )
        return Uri.parse(path)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}