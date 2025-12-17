package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransaksiAdapter(
    private val transaksiList: List<Transaksi>
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        private val tvDeskripsi: TextView = itemView.findViewById(R.id.tvDeskripsi)
        private val tvPoin: TextView = itemView.findViewById(R.id.tvPoin)

        fun bind(transaksi: Transaksi) {
            tvTanggal.text = transaksi.tanggal
            tvDeskripsi.text = transaksi.deskripsi
            tvPoin.text = transaksi.poin

            // Ubah warna berdasarkan tipe transaksi
            if (transaksi.isMasuk) {
                tvPoin.setTextColor(Color.parseColor("#4CAF50")) // Hijau
            } else {
                tvPoin.setTextColor(Color.parseColor("#F44336")) // Merah
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transaksiList[position])
    }

    override fun getItemCount(): Int = transaksiList.size
}
