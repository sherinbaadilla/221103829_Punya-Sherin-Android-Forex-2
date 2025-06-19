package com.sherin1234.forex2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ForexAdapter extends RecyclerView.Adapter<ForexViewHolder> {

    private List<ForexModel> forexModelList;

    public ForexAdapter(List<ForexModel> forexModelList) {
        this.forexModelList = forexModelList;
    }

    @NonNull
    @Override
    public ForexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_forex, parent, false);
        return new ForexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForexViewHolder holder, int position) {
        ForexModel item = forexModelList.get(position);

        // Mengisi data ke tampilan
        holder.kodeTextView.setText(item.code); // Contoh: USD
        holder.namaTextView.setText(item.name); // Contoh: United States Dollar
        holder.kursTextView.setText(String.format(Locale.US, "%,.2f", item.rate)); // Contoh: 15,123.45
    }

    @Override
    public int getItemCount() {
        return forexModelList.size();
    }
}
