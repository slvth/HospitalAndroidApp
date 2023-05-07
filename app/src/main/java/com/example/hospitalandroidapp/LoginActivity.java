package com.example.hospitalandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Connection connection;
    TextInputEditText edtLoginAdress, edtPasswordAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLoginAdress = findViewById(R.id.edtLoginAdress);
        edtPasswordAddress = findViewById(R.id.edtPasswordAddress);

        //лень вводить, !ПОТОМ УДАЛИТЬ
        edtLoginAdress.setText("1");
        edtPasswordAddress.setText("1");

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        TextView txtResetPassword = findViewById(R.id.txtResetPassword);
        txtResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "efsefse", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn(){
        if(!isValidate()) //проверка на заполненность
            return;
        String login = edtLoginAdress.getText().toString();
        String password = edtPasswordAddress.getText().toString();

        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        int user_id = -1;

        if(connection != null) {
            String sqlQueryFindUser = "Select [код пациента] from пользователи where логин ='" + login + "' and пароль='" + password + "' and [тип пользователя]='пациент'";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQueryFindUser);
                while (set.next()) {
                    user_id = set.getInt(1);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            if(user_id==-1) { //проверка, есть ли такой пользователь
                Toast.makeText(this, "Неправильный логин или пароль!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Успешная авторизация!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private Boolean isValidate(){
        if(edtLoginAdress.getText().toString().equals("") || edtPasswordAddress.getText().toString().equals("")){
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}