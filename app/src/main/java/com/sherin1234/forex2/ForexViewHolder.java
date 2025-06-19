package com.sherin1234.forex2;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ForexViewHolder extends RecyclerView.ViewHolder {
    public TextView kodeTextView, kursTextView, namaTextView;

    public ForexViewHolder(View itemView) {
        super(itemView);

        kodeTextView = itemView.findViewById(R.id.kodeTextView);
        namaTextView = itemView.findViewById(R.id.namaTextView);
        kursTextView = itemView.findViewById(R.id.kursTextView);
    }
}
