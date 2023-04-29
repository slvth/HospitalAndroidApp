package com.example.hospitalandroidapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hospitalandroidapp.AddRecordActivity;
import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.adapter.RecordAdapter;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.example.hospitalandroidapp.database.RecordReceptionModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class RecordFragment extends Fragment {
    Connection connection;
    RecyclerView recyclerViewRecord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        Button btnRecord = view.findViewById(R.id.btnRecord);
        recyclerViewRecord = view.findViewById(R.id.recyclerViewRecord);

        downloadDataToRecyclerview(); //загрузка списка записей из бд в Recyclerview

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRecordActivity.class);
                startActivityForResult(intent, 100);

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
            downloadDataToRecyclerview();
    }

    private void downloadDataToRecyclerview(){
        ArrayList<RecordReceptionModel> records = new ArrayList<>();
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        if(connection != null){
            String sqlQuery = "Select з.*, в.фамилия+' '+в.имя+' '+в.отчество фио from [запись на прием] з, [врач] в where з.[код врача]=в.[код врача] order by з.[дата и время]";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    records.add(new RecordReceptionModel(set.getInt(1), set.getInt(2), set.getInt(3), set.getString(4), set.getString(5)));
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
        recyclerViewRecord.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecord.setAdapter(new RecordAdapter(getActivity(), records));
    }
}