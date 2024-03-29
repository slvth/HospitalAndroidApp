package com.example.hospitalandroidapp.activity;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalandroidapp.MainActivity;
import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {
    Connection connection;
    TextInputEditText edtLoginAdress, edtPasswordAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLoginAdress = findViewById(R.id.edtLoginAdress);
        edtPasswordAddress = findViewById(R.id.edtPasswordAddress);

        //логин и пароль пациента
        edtLoginAdress.setText("nikitin1234@mail.ru");
        edtPasswordAddress.setText("1234nikitin");

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
                startActivityForResult(new Intent(LoginActivity.this, ResetPasswordActivity.class),101);
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
            //String sqlQueryFindUser = "Select [код пациента] from пользователи where логин ='" + login + "' and пароль='" + password + "' and [тип пользователя]='пациент'";
            String sqlQueryFindUser = "Select p.[код пациента] from пользователи u, пациент p " +
                    "where u.фамилия+' '+u.имя+' '+u.отчество = p.фамилия+' '+p.имя+' '+p.отчество " +
                    "and логин ='"+login+"' and пароль='"+password+"' and роль='пациент'";

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

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.pref_id), user_id);
            editor.apply();

            Toast.makeText(this, "Успешная авторизация!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Boolean isValidate(){
        if(edtLoginAdress.getText().toString().equals("") || edtPasswordAddress.getText().toString().equals("")){
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && data!=null)
            finish();
    }
}