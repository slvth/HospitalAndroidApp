package com.example.hospitalandroidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddRecordActivity extends AppCompatActivity {
    Connection connection;
    Spinner spinnerProfession, spinnerDoctor;
    EditText editTextDateAddRecord;
    Calendar dateAndTime= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        Locale.setDefault(new Locale("RU"));

        spinnerProfession = findViewById(R.id.spinnerProfession);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        editTextDateAddRecord = findViewById(R.id.editTextDateAddRecord);
        ImageButton imgButtonDateAddRecord = findViewById(R.id.imgButtonDateAddRecord);
        Button btnBackAddRecord = findViewById(R.id.btnBackAddRecord);
        Button btnSaveAddRecord = findViewById(R.id.btnSaveAddRecord);

        loadSpinnerProfession();
        setInitialDateTime();

        imgButtonDateAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

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
                    Toast.makeText(AddRecordActivity.this, "Ошибка при соединении с БД или не заполнены данные", Toast.LENGTH_SHORT).show();
                    return;
                }

                //проверка на наличие записи на существующее время
                if(!isFreeRecord()){
                    Toast.makeText(AddRecordActivity.this, "Запись на это время уже занята! Выберите другое время.", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveRecord();
            }
        });
    }

    private Boolean isValidate(){
        return spinnerDoctor.getSelectedItem()!=null
                && spinnerProfession.getSelectedItem()!=null;
    }

    private Boolean isFreeRecord(){
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.ENGLISH);
        String selectedDateTime = dateFormat.format(dateAndTime.getTime());
        String doctorFIO = spinnerDoctor.getSelectedItem().toString();
        int doctorID = 0;

        if(connection != null) {
            String sqlQueryFindDoctorID = "SELECT d.[код врача], d.фамилия+' '+d.имя+' '+d.отчество as fio " +
                    "from [врач] d,[запись на прием] wr " +
                    "where wr.[код врача]=d.[код врача] " +
                    "and d.фамилия+' '+d.имя+' '+d.отчество = '"+doctorFIO+"' " +
                    "and wr.[дата и время]='"+selectedDateTime+"'";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQueryFindDoctorID);
                while (set.next()) {
                    doctorID = set.getInt(1);
                }
                if(doctorID!=0)
                    return false;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
        return true;
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.ENGLISH);
            String selectedDateTime = dateFormat.format(dateAndTime.getTime());
            String sqlQuery = "Insert into [запись на прием]([код врача], [код пациента], [дата и время]) " +
                    "values ('"+doctorID+"','"+1+"', '"+selectedDateTime+"')";
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
        ArrayList<String> data = new ArrayList<>();

        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        if(connection != null){
            String sqlQuery = "Select * from [специальность]";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    data.add(set.getString(2));
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
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
            public void onNothingSelected(AdapterView<?> arg0) {
            }
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
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
        String currentTime = dateFormat.format(dateAndTime.getTime());
        editTextDateAddRecord.setText(currentTime);
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }


    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();

        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
            setTime(view);
        }
    };

}