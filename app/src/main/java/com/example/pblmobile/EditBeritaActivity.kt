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
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.EditResponse
import com.example.pblmobile.databinding.EditberitaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class EditBeritaActivity : AppCompatActivity() {

    private lateinit var binding: EditberitaBinding
    private val IMAGE_REQUEST_CODE = 1000
    private var encodedImage: String? = null // Gambar dalam format Base64

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditberitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        val beritaId = intent.getStringExtra("id_berita") ?: ""
        val judul = intent.getStringExtra("judul") ?: ""
        val gambar = intent.getStringExtra("gambar")
        val deskripsi = intent.getStringExtra("deskripsi") ?: ""
        val detail = intent.getStringExtra("detail") ?: ""

        // Isi data ke form
        binding.etBeritaTitle.setText(judul)
        binding.etBeritaDescription.setText(deskripsi)
        binding.etBeritaDetail.setText(detail)

        // Tampilkan gambar jika ada
        if (!gambar.isNullOrEmpty()) {
            Glide.with(this)
                .load(gambar)
                .placeholder(R.drawable.masukkangambar) // Placeholder jika gambar gagal dimuat
                .into(binding.ivBeritaImage)
        } else {
            // Tampilkan gambar default jika `gambar` kosong
            binding.ivBeritaImage.setImageResource(R.drawable.masukkangambar)
        }

        // Pilih gambar dari galeri
        binding.ivBeritaImage.setOnClickListener {
            openGalleryForImage()
        }

        // Tombol kembali
        binding.tvBack.setOnClickListener {
            onBackPressed()
        }

        // Submit data
        binding.btnSubmit.setOnClickListener {
            val updatedTitle = binding.etBeritaTitle.text.toString()
            val updatedDescription = binding.etBeritaDescription.text.toString()
            val updatedDetail = binding.etBeritaDetail.text.toString()

            if (updatedTitle.isNotEmpty() && updatedDescription.isNotEmpty() && updatedDetail.isNotEmpty()) {
                val requestBody = mutableMapOf(
                    "id_berita" to beritaId,
                    "judul" to updatedTitle,
                    "gambar" to (encodedImage ?: gambar ?: ""), // Gunakan gambar baru atau yang lama
                    "deskripsi" to updatedDescription,
                    "detail" to updatedDetail
                )
                updateBerita(requestBody)
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
                // Tampilkan gambar di ImageView
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivBeritaImage)

                // Encode gambar ke Base64
                encodedImage = encodeImageToBase64(uri)
            } ?: run {
                Toast.makeText(this, "Gambar tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun updateBerita(requestBody: Map<String, String>) {
        RetrofitClient.instance.editBerita(requestBody)
            .enqueue(object : Callback<EditResponse> {
                override fun onResponse(call: Call<EditResponse>, response: Response<EditResponse>) {
                    if (response.isSuccessful) {
                        val editResponse = response.body()
                        if (editResponse != null && editResponse.status) {
                            Toast.makeText(this@EditBeritaActivity, "Berita berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditBeritaActivity, "Gagal memperbarui berita: ${editResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@EditBeritaActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<EditResponse>, t: Throwable) {
                    Toast.makeText(this@EditBeritaActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
