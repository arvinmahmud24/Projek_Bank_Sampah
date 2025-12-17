package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SampahAdapter extends RecyclerView.Adapter<SampahAdapter.ViewHolder> {

    private List<Sampah> sampahList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sampah sampah);
    }

    public SampahAdapter(List<Sampah> sampahList, OnItemClickListener listener) {
        this.sampahList = sampahList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sampah, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sampah item = sampahList.get(position);
        holder.namaSampah.setText(item.getNama());
        holder.hargaSampah.setText(item.getHarga());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return sampahList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaSampah, hargaSampah;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaSampah = itemView.findViewById(R.id.textViewNamaSampah);
            hargaSampah = itemView.findViewById(R.id.textViewHargaSampah);
        }
    }
}
