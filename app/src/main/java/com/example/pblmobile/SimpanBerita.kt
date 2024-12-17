package com.example.pblmobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pblmobile.Models.Beritaku
import com.example.pblmobile.Models.BeritakuResponse
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.databinding.SimpanBeritaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class SimpanBerita : AppCompatActivity() {

    private lateinit var binding: SimpanBeritaBinding // Initialize binding
    private val IMAGE_REQUEST_CODE = 1000 // Request code for image selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SimpanBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.tvBack.setOnClickListener {
            onBackPressed()
        }

        // Handle the ImageView click to open gallery
        binding.ivBeritaImage.setOnClickListener {
            openGalleryForImage()
        }

        // Handle form submission
        binding.btnSubmit.setOnClickListener {
            val judul = binding.etBeritaTitle.text.toString()
            val deskripsi = binding.etBeritaDescription.text.toString()
            val detail = binding.etBeritaDetail.text.toString()

            if (judul.isNotEmpty() && deskripsi.isNotEmpty() && detail.isNotEmpty()) {
                val uri = binding.ivBeritaImage.tag as? String
                if (uri != null) {
                    val base64Image = encodeImageToBase64(Uri.parse(uri))
                    if (base64Image.isNotEmpty()) {
                        // Create the Beritaku object
                        val berita = Beritaku(
                            judul = judul,
                            deskripsi = deskripsi,
                            detail = detail,
                            gambar = base64Image
                        )
                        simpanBerita(berita)
                    } else {
                        Toast.makeText(this, "Gambar gagal diproses", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            data?.data?.let { uri ->
                binding.ivBeritaImage.tag = uri.toString()

                // Display the selected image in ImageView
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivBeritaImage)
            } ?: run {
                Toast.makeText(this, "Gambar tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageToBase64(uri: Uri): String {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun simpanBerita(beritaku: Beritaku) {
        val call = RetrofitClient.instance.simpanBerita(beritaku)
        call.enqueue(object : Callback<BeritakuResponse> {
            override fun onResponse(call: Call<BeritakuResponse>, response: Response<BeritakuResponse>) {
                if (response.isSuccessful) {
                    val beritaResponse = response.body()
                    if (beritaResponse != null && beritaResponse.status) {
                        Toast.makeText(this@SimpanBerita, "Berita berhasil disimpan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SimpanBerita, "Gagal menyimpan berita: ${beritaResponse?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@SimpanBerita, "Terjadi kesalahan: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BeritakuResponse>, t: Throwable) {
                Toast.makeText(this@SimpanBerita, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
