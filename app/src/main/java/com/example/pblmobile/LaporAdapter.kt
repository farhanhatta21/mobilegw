package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.DeleteResponse
import com.example.pblmobile.Models.Laporan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaporAdapter(
    private val context: Context,
    private val laporanList: List<Laporan>
) : RecyclerView.Adapter<LaporAdapter.LaporViewHolder>() {

    class LaporViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val ivBukti: ImageView = itemView.findViewById(R.id.ivBukti)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lapor, parent, false)
        return LaporViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporViewHolder, position: Int) {
        val item = laporanList[position]
        holder.tvNama.text = item.nama
        holder.tvLokasi.text = "Lokasi: ${item.lokasi}"

        // Muat gambar menggunakan Glide
        Glide.with(context)
            .load(item.bukti)
            .placeholder(R.drawable.loading)
            .error(R.drawable.gambartdktersedia)
            .into(holder.ivBukti)

        // Tambahkan long press listener
        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, item)
            true
        }
    }

    private fun showPopupMenu(view: View, laporan: Laporan) {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_laporan, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    deleteLaporan(laporan)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun deleteLaporan(laporan: Laporan) {
        val requestBody = mapOf("id_lapor" to laporan.id_lapor.toString())

        RetrofitClient.instance.deleteLaporan(requestBody).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Laporan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, Menuutama::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Gagal menghapus laporan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(context, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = laporanList.size
}