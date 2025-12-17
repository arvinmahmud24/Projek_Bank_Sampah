package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class BankSampahAdapter(
    private val bankSampahList: List<BankSampah>,
    private val onItemClick: (BankSampah) -> Unit
) : RecyclerView.Adapter<BankSampahAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaBank: TextView = itemView.findViewById(R.id.textViewNamaBank)
        private val alamatBank: TextView = itemView.findViewById(R.id.textViewAlamatBank)
        private val jarakBank: TextView = itemView.findViewById(R.id.textViewJarak)

        fun bind(bankSampah: BankSampah) {
            namaBank.text = bankSampah.nama
            alamatBank.text = bankSampah.alamat

            // Format Jarak
            jarakBank.text = if (bankSampah.jarak >= 1000) {
                String.format(Locale.getDefault(), "%.1f km", bankSampah.jarak / 1000)
            } else {
                String.format(Locale.getDefault(), "%.0f m", bankSampah.jarak)
            }

            itemView.setOnClickListener { onItemClick(bankSampah) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bank_sampah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bankSampahList[position])
    }

    override fun getItemCount(): Int = bankSampahList.size
}
