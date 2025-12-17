package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<Transaksi> transaksiList;

    public TransaksiAdapter(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi item = transaksiList.get(position);
        holder.tvTanggal.setText(item.getTanggal());
        holder.tvDeskripsi.setText(item.getDeskripsi());
        holder.tvPoin.setText(item.getPoin());

        // Ubah warna berdasarkan tipe transaksi
        if (item.isMasuk()) {
            holder.tvPoin.setTextColor(Color.parseColor("#4CAF50")); // Hijau
        } else {
            holder.tvPoin.setTextColor(Color.parseColor("#F44336")); // Merah
        }
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvDeskripsi, tvPoin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvPoin = itemView.findViewById(R.id.tvPoin);
        }
    }
}
