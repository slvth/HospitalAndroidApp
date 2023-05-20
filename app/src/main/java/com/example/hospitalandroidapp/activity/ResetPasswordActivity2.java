package com.example.hospitalandroidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ResetPasswordActivity2 extends AppCompatActivity {

    Connection connection;
    TextInputEditText edtLoginReset, edtPasswordReset, edtPasswordRepeat;
    Button btnResetAccount;
    ImageButton btnResetBack2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password2);

        int pacient_id = getIntent().getIntExtra("pacient_id", 0);

        edtLoginReset = findViewById(R.id.edtLoginReset);
        edtPasswordReset = findViewById(R.id.edtPasswordReset);
        edtPasswordRepeat = findViewById(R.id.edtPasswordRepeat);

        btnResetAccount = findViewById(R.id.btnResetAccount);
        btnResetBack2 = findViewById(R.id.btnResetBack2);
        btnResetAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidate())
                    return;
                if(!isOnlyLogin())
                    return;

                String login = edtLoginReset.getText().toString();
                String password = edtPasswordReset.getText().toString();

                saveNewLogin(pacient_id, login, password);

                Toast.makeText(ResetPasswordActivity2.this, "Успешно!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ResetPasswordActivity2.this, LoginActivity.class));
                finish();
            }
        });

        btnResetBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveNewLogin(int pacient_id, String login, String password) {
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        if(connection != null) {
            Statement statement = null;
            String sqlQuery = "UPDATE пользователи " +
                    "SET логин='"+login+"', " +
                    "пароль='"+password+"' " +
                    "WHERE [код пациента]="+pacient_id;
            try {
                statement = connection.createStatement();
                statement.executeQuery(sqlQuery);
                connection.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
    }

    private boolean isValidate() {
        if(edtLoginReset.getText().toString().equals("") || edtPasswordReset.toString().equals("") || edtPasswordRepeat.getText().toString().equals("")){
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!edtPasswordReset.getText().toString().equals(edtPasswordRepeat.getText().toString())){
            Toast.makeText(this, "Поле \"Пароль\" не соотвествует полю \"Повторите пароль\"!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isOnlyLogin() {
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        int user_id = -1;
        String login = edtLoginReset.getText().toString();

        if(connection != null) {
            String sqlQueryFindUser = "Select [код пользователя] from пользователи where логин ='" + login+"'";
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
            if (user_id != -1) { //проверка, есть ли такой логин
                Toast.makeText(this, "Такой логин уже существует! Выберите другой", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

}