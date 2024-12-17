package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pblmobile.Models.BeritaItem
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.DeleteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaAdapter(
    private val context: Context,
    private val items: MutableList<BeritaItem>
) : RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    class BeritaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewBerita)
        val titleTextView: TextView = view.findViewById(R.id.textViewBeritaTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.textViewBeritaDescription)
        val timeTextView: TextView = view.findViewById(R.id.textViewBeritaTime)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton) // Tombol untuk delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_berita_card, parent, false)
        return BeritaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        val berita = items[position]

        val imageUrl = "https://tkj-3b.com/api/kelompok_1/${berita.gambar}"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.loadings)
            .error(R.drawable.gambartdktersedia)
            .into(holder.imageView)

        holder.titleTextView.text = berita.judul
        holder.descriptionTextView.text = berita.deskripsi
        holder.timeTextView.text = berita.waktu

        // Tombol delete dengan konfirmasi
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(berita.id_berita)
        }

        // Long click untuk menampilkan opsi edit dan delete
        holder.itemView.setOnLongClickListener {
            showOptionsDialog(berita.id_berita)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    // Method untuk update data berita
    fun updateItems(newItems: List<BeritaItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(id_berita: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus berita ini?")

        builder.setPositiveButton("Ya") { _, _ ->
            // Menghapus berita dari server
            deleteBerita(id_berita)
        }

        builder.setNegativeButton("Batal", null)

        builder.show()
    }


    private fun showOptionsDialog(id_berita: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pilih Aksi")
        builder.setItems(arrayOf("Edit", "Hapus")) { _, which ->
            when (which) {
                0 -> {
                    // Logic for editing the berita
                    editBerita(id_berita)
                }
                1 -> {
                    // Logic for deleting the berita
                    showDeleteConfirmationDialog(id_berita)
                }
            }
        }
        builder.show()
    }

    private fun editBerita(id_berita: String) {
        // Add your logic to edit the berita
        Toast.makeText(context, "Edit berita: $id_berita", Toast.LENGTH_SHORT).show()
    }

    private fun deleteBerita(id_berita: String) {
        RetrofitClient.instance.deleteBerita(id_berita).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse != null && deleteResponse.status) {
                        Toast.makeText(context, "Berita berhasil dihapus", Toast.LENGTH_SHORT).show()
                        // Navigasi ke Menuadmin setelah berhasil
                        val intent = Intent(context, Menuadmin::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Gagal menghapus berita", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }








}
