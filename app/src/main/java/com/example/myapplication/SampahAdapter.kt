package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SampahAdapter(
    private val sampahList: List<Sampah>,
    private val onItemClick: (Sampah) -> Unit
) : RecyclerView.Adapter<SampahAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaSampah: TextView = itemView.findViewById(R.id.textViewNamaSampah)
        private val hargaSampah: TextView = itemView.findViewById(R.id.textViewHargaSampah)

        fun bind(sampah: Sampah) {
            namaSampah.text = sampah.nama
            hargaSampah.text = sampah.harga
            itemView.setOnClickListener { onItemClick(sampah) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sampah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sampahList[position])
    }

    override fun getItemCount(): Int = sampahList.size
}
