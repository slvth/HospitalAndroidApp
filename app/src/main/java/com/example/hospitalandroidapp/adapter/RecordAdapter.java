package com.example.hospitalandroidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.RecordReceptionModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    Context context;
    ArrayList<RecordReceptionModel> records;

    public RecordAdapter(Context context, ArrayList<RecordReceptionModel> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("ru"));
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
        String dateInString = records.get(position).getDate();
        Date dateParse = null;
        try {
            dateParse = formatInput.parse(dateInString);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String date = formatOutput.format(dateParse);
        String doctor = "Врач: "+records.get(position).getDoctorFIO();

        holder.txtDateItemRecord.setText(date);
        holder.txtDoctorItemRecord.setText(doctor);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}


