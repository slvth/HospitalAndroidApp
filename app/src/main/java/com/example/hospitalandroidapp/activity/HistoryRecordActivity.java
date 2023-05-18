package com.example.hospitalandroidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.adapter.RecordAdapter;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.example.hospitalandroidapp.database.RecordReceptionModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class HistoryRecordActivity extends AppCompatActivity {
    Connection connection;
    RecyclerView recyclerViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        ImageButton btnHistoryBack = findViewById(R.id.btnHistoryBack);

        //загрузка списка записей из бд в Recyclerview
        downloadDataToRecyclerview();

        btnHistoryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void downloadDataToRecyclerview(){
        ArrayList<RecordReceptionModel> records = new ArrayList<>();
        ConnectionSQL connectionSQL = new ConnectionSQL();

        connection = connectionSQL.connectionClass();
        if(connection != null){
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
            int pacient_id = sharedPref.getInt(getString(R.string.pref_id), 0);

            String sqlQuery = "Select wr.*, d.фамилия+' '+d.имя+' '+d.отчество+'\n('+sp.название+')' фио " +
                    "from [запись на прием] wr, [врач] d, специальность sp " +
                    "where wr.[код врача]=d.[код врача] and sp.[код специальности]=d.[код специальности] " +
                    "and wr.[дата и время]<GETDATE() and [код пациента]="+pacient_id+" " +
                    "order by wr.[дата и время] desc";

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
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(new RecordAdapter(this, records, false));
    }

}