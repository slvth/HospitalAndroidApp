package com.example.hospitalandroidapp.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalandroidapp.R;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView txtDateItemRecord, txtDoctorItemRecord;
    ImageButton btnDeleteRecord;

    public RecordViewHolder(@NonNull View item) {
        super(item);
        txtDateItemRecord = item.findViewById(R.id.txtDateItemRecord);
        txtDoctorItemRecord = item.findViewById(R.id.txtDoctorItemRecord);
        btnDeleteRecord = item.findViewById(R.id.btnDeleteRecord);
    }
}
