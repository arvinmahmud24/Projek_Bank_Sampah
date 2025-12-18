package com.example.myapplication

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SampahAdapter(
    private val sampahList: List<Sampah>,
    private val onTotalChanged: (Double) -> Unit
) : RecyclerView.Adapter<SampahAdapter.ViewHolder>() {

    private val beratMap = mutableMapOf<Int, Double>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaSampah: TextView = itemView.findViewById(R.id.textViewNamaSampah)
        val hargaSampah: TextView = itemView.findViewById(R.id.textViewHargaSampah)
        val etBerat: EditText = itemView.findViewById(R.id.editTextBerat)

        private var textWatcher: TextWatcher? = null

        fun bind(sampah: Sampah, position: Int) {
            namaSampah.text = sampah.nama
            hargaSampah.text = sampah.harga
            
            // Bersihkan TextWatcher lama agar tidak terjadi penumpukan
            etBerat.removeTextChangedListener(textWatcher)
            
            // Set nilai berat dari map jika ada
            val currentBerat = beratMap[position] ?: 0.0
            etBerat.setText(if (currentBerat > 0) currentBerat.toString() else "")

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val berat = s.toString().toDoubleOrNull() ?: 0.0
                    beratMap[position] = berat
                    calculateTotal()
                }
                override fun afterTextChanged(s: Editable?) {}
            }
            etBerat.addTextChangedListener(textWatcher)
        }
    }

    private fun calculateTotal() {
        var grandTotal = 0.0
        beratMap.forEach { (position, berat) ->
            val hargaString = sampahList[position].harga.replace("Rp. ", "").replace(",", "")
            val harga = hargaString.toDoubleOrNull() ?: 0.0
            grandTotal += berat * harga
        }
        onTotalChanged(grandTotal)
    }

    fun getSetoranData(): Map<Int, Double> = beratMap

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sampah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sampahList[position], position)
    }

    override fun getItemCount(): Int = sampahList.size
}
