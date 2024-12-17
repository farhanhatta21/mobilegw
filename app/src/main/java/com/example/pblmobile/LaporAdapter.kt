package com.example.pblmobile

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pblmobile.Models.Laporan

class LaporAdapter(private val laporanList: List<Laporan>) :
    RecyclerView.Adapter<LaporAdapter.LaporViewHolder>() {

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

        // Tampilkan placeholder terlebih dahulu
        Glide.with(holder.itemView.context)
            .load(R.drawable.loading) // Placeholder image langsung
            .into(holder.ivBukti)

        // Setelah 2 detik, muat gambar sebenarnya
        Handler(Looper.getMainLooper()).postDelayed({
            Glide.with(holder.itemView.context)
                .load(item.bukti) // URL atau Base64 string
                .error(R.drawable.gambartdktersedia) // Gambar error
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Log error jika diperlukan
                        e?.logRootCauses("Glide Load Error")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(holder.ivBukti)
        }, 1600) // 2 detik
    }

    override fun getItemCount(): Int = laporanList.size
}
