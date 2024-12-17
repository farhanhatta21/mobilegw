package com.example.pblmobile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.LaporResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Pelaporan : AppCompatActivity() {
    private var selectedImageUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var etBuktifotoImageView: ImageView
    private val LOCATION_REQUEST_CODE = 101
    private val CAMERA_PERMISSION_REQUEST_CODE = 102

    // Register activity result launchers for gallery and camera
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            etBuktifotoImageView.setImageURI(it) // Display selected image
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedImageUri = saveBitmapToFile(it) // Save bitmap and get URI
            etBuktifotoImageView.setImageBitmap(it) // Display captured image
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pelaporan)

        // Initialize views
        etBuktifotoImageView = findViewById(R.id.etBuktifoto)

        // Set up image picker and camera
        etBuktifotoImageView.setOnClickListener {
            showImagePickerDialog()
        }

        val lokasi = findViewById<TextView>(R.id.etLokasi)
        lokasi.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }

        val backbutton = findViewById<TextView>(R.id.tvback)
        backbutton.setOnClickListener {
            val intent = Intent(this, Menuutama::class.java)
            startActivity(intent)
        }

        // Submit button functionality
        findViewById<Button>(R.id.btnSubmit)?.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermissionAndTakePhoto()
                1 -> pickImageLauncher.launch("image/*")
            }
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_with_shadow) // Terapkan background
        dialog.show()
    }


    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePictureLauncher.launch(null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return Uri.fromFile(file)
    }

    private fun showConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            dialog.dismiss()
            submitReport()
            startActivity(Intent(this, Menuutama::class.java))
            finish()
        }

        dialog.show()
    }

    private fun submitReport() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val idAkun = sharedPreferences.getInt("id_akun", -1)
        val nama = sharedPreferences.getString("user_name", "") ?: ""
        val lokasi = findViewById<EditText>(R.id.etLokasi)?.text.toString()

        if (nama.isEmpty() || lokasi.isEmpty() || selectedImageUri == null || latitude == null || longitude == null) {
            Toast.makeText(this, "All fields are required, including location and an image.", Toast.LENGTH_LONG).show()
            return
        }

        if (idAkun == -1) {
            Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_LONG).show()
            return
        }

        val buktiBase64 = encodeImageToBase64(selectedImageUri!!)
        val requestBody = mapOf(
            "id_akun" to idAkun.toString(),
            "nama" to nama,
            "lokasi" to lokasi,
            "latitude" to latitude.toString(),
            "longitude" to longitude.toString(),
            "bukti" to buktiBase64
        )

        RetrofitClient.instance.reportTrash(requestBody).enqueue(object : Callback<LaporResponse> {
            override fun onResponse(call: Call<LaporResponse>, response: Response<LaporResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status) {
                            Toast.makeText(this@Pelaporan, "Report submitted successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@Pelaporan, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this@Pelaporan, "Server error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LaporResponse>, t: Throwable) {
                Toast.makeText(this@Pelaporan, "Submission failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun encodeImageToBase64(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(this, "Camera permission is required to take a photo.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            val locationDetails = data?.getStringExtra("LOCATION_DETAILS")
            latitude = data?.getDoubleExtra("LATITUDE", 0.0)
            longitude = data?.getDoubleExtra("LONGITUDE", 0.0)

            if (!locationDetails.isNullOrEmpty() && latitude != 0.0 && longitude != 0.0) {
                findViewById<EditText>(R.id.etLokasi).setText(locationDetails)
            } else {
                Toast.makeText(this, "Invalid or no location selected.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
