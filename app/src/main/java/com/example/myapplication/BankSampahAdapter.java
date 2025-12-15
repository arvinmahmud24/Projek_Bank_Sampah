package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class BankSampahAdapter extends RecyclerView.Adapter<BankSampahAdapter.ViewHolder> {

    private List<BankSampah> bankSampahList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BankSampah bankSampah);
    }

    public BankSampahAdapter(List<BankSampah> bankSampahList, OnItemClickListener listener) {
        this.bankSampahList = bankSampahList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_sampah, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BankSampah item = bankSampahList.get(position);
        holder.namaBank.setText(item.getNama());
        holder.alamatBank.setText(item.getAlamat());

        // Format Jarak
        float jarakMeter = item.getJarak();
        if (jarakMeter >= 1000) {
            holder.jarakBank.setText(String.format(Locale.getDefault(), "%.1f km", jarakMeter / 1000));
        } else {
            holder.jarakBank.setText(String.format(Locale.getDefault(), "%.0f m", jarakMeter));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return bankSampahList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaBank, alamatBank, jarakBank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaBank = itemView.findViewById(R.id.textViewNamaBank);
            alamatBank = itemView.findViewById(R.id.textViewAlamatBank);
            jarakBank = itemView.findViewById(R.id.textViewJarak);
        }
    }
}
