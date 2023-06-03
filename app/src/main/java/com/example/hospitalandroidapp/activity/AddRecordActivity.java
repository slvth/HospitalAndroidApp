package com.example.hospitalandroidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddRecordActivity extends AppCompatActivity {
    Connection connection;
    Spinner spinnerProfession, spinnerDoctor, spinnerDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        Locale.setDefault(new Locale("RU"));

        spinnerProfession = findViewById(R.id.spinnerProfession);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        spinnerDateTime = findViewById(R.id.spinnerDateTime);
        Button btnBackAddRecord = findViewById(R.id.btnBackAddRecord);
        Button btnSaveAddRecord = findViewById(R.id.btnSaveAddRecord);

        loadSpinnerProfession();

        btnBackAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        btnSaveAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //проверка на заполненность
                if(!isValidate()){
                    Toast.makeText(AddRecordActivity.this, "Данные не заполнены или нет доступных записей!", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveRecord();
            }
        });
    }

    private Boolean isValidate(){
        return spinnerDoctor.getSelectedItem()!=null && spinnerProfession.getSelectedItem()!=null
                && spinnerDateTime.getSelectedItem()!=null;
    }


    private void saveRecord(){
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        String doctorFIO = spinnerDoctor.getSelectedItem().toString();
        int doctorID = 0;

        if(connection != null){
            String sqlQueryFindDoctorID = "SELECT d.[код врача], d.фамилия+' '+d.имя+' '+d.отчество as fio from [врач] d " +
                    "where d.фамилия+' '+d.имя+' '+d.отчество = '"+doctorFIO+"' ";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQueryFindDoctorID);
                while (set.next()){
                    doctorID = set.getInt(1);
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            //взятие текущего авторизованного пользователя
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
            int pacient_id = sharedPref.getInt(getString(R.string.pref_id), 0);

            //форматирование даты и времени
            SimpleDateFormat formatInput = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
            Date dateParse = null;
            try {
                dateParse = formatInput.parse(spinnerDateTime.getSelectedItem().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",  new Locale("ru"));
            String selectedDate = dateFormat.format(Objects.requireNonNull(dateParse));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm",  new Locale("ru"));
            String selectedTime = timeFormat.format(dateParse);

            //sql запрос
            String sqlQuery = "UPDATE [запись на прием] " +
                    "SET [код пациента] = '"+pacient_id +"' "+
                    "WHERE [код врача] = '"+doctorID+"' and"+
                    "[дата] = '"+selectedDate+"' and "+
                    "[время] = '"+selectedTime+"'";
            try {
                statement = connection.createStatement();
                statement.executeQuery(sqlQuery);
                connection.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private void loadSpinnerProfession(){
        ArrayList<String> professions = new ArrayList<>();

        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        if(connection != null){
            String sqlQuery = "SELECT [код специальности],[название] FROM [специальность]";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    professions.add(set.getString(2));
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, professions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProfession.setAdapter(adapter);
        spinnerProfession.setPrompt("Выберите специальность");
        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                loadSpinnerDoctor();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    private void loadSpinnerDoctor(){
        ArrayList<String> data = new ArrayList<>();
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        if(connection != null){
            String sqlQuery = "Select d.фамилия+' '+d.имя+' '+d.отчество фио from [врач] d, [специальность] p " +
                    "where d.[код специальности]=p.[код специальности] and p.название = '"+spinnerProfession.getSelectedItem().toString()+"'";

            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    data.add(set.getString(1));
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDoctor.setAdapter(adapter);
        spinnerDoctor.setPrompt("Выберите врача");
        spinnerDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadSpinnerDatetime();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loadSpinnerDatetime(){
        ArrayList<String> data = new ArrayList<>();
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        if(connection != null){
            String sqlQuery = "Select wr.[код записи на прием], " +
                    "CAST(wr.[дата] as DATETIME) + CAST(CAST(wr.[время] AS TIME) as DATETIME) as Дата " +
                    "from [врач] d, [запись на прием] wr " +
                    "where d.[код врача]=wr.[код врача] and wr.[код пациента] IS NULL " +
                    "and d.фамилия+' '+d.имя+' '+d.отчество ='"+spinnerDoctor.getSelectedItem().toString()+"'" +
                    "order by CAST(wr.[дата] as DATETIME) + CAST(CAST(wr.[время] AS TIME) as DATETIME)";

            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("ru"));
                    SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
                    Date dateParse = null;
                    try {
                        dateParse = formatInput.parse(set.getString(2));

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    String date = formatOutput.format(dateParse);
                    data.add(date);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDateTime.setAdapter(adapter);
        spinnerDateTime.setPrompt("Выберите дату и время");
    }
}