package com.example.hospitalandroidapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.santalu.maskara.widget.MaskEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ResetPasswordActivity extends AppCompatActivity {
    Connection connection;
    Calendar dateAndTime= Calendar.getInstance();
    LinearLayout linearResetPassport,linearResetPhone;

    TextView txtCheckBirthday;
    MaskEditText edtResetPhone, edtResetPassport;

    private final int MY_PERMESSIONS_REQUEST_SMS=100;
    private final int MY_PERMESSIONS_RECEIVE_SMS=200;

    private String numberPhone = "+1-555-521-5554"; //номер телефона у эмулятора
    private String passport = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        linearResetPassport = findViewById(R.id.linearResetPassport);
        linearResetPhone = findViewById(R.id.linearResetPhone);

        edtResetPhone = findViewById(R.id.edtResetPhone);
        edtResetPassport = findViewById(R.id.edtResetPassport);

        RadioButton rbtnResetPhone = findViewById(R.id.rbtnResetPhone);
        RadioButton rbtnResetPassport = findViewById(R.id.rbtnResetPassport);
        Button btnCheckReset = findViewById(R.id.btnCheckReset);
        ImageButton btnResetBack = findViewById(R.id.btnResetBack);

        goneLinear(true);
        rbtnResetPhone.setChecked(true);

        rbtnResetPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goneLinear(true);
            }
        });

        rbtnResetPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goneLinear(false);
            }
        });

        btnCheckReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(ResetPasswordActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ResetPasswordActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMESSIONS_REQUEST_SMS);
                    ActivityCompat.requestPermissions(ResetPasswordActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMESSIONS_RECEIVE_SMS);
                    return;
                }

                if(rbtnResetPhone.isChecked() && isValidatePhone()){
                    if(findPacient()!=-1)
                        alertDialogCheckCode();
                }
                else if(rbtnResetPassport.isChecked() && isValidatePassport()) {
                    if (findPacient() != -1)
                        alertDialogCheckPassport();
                }
            }
        });

        btnResetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void goneLinear(Boolean isPhone){
        edtResetPhone.setText("");
        edtResetPassport.setText("");

        if(isPhone){
            linearResetPassport.setVisibility(View.GONE);
            linearResetPhone.setVisibility(View.VISIBLE);
        }
        else{
            linearResetPassport.setVisibility(View.VISIBLE);
            linearResetPhone.setVisibility(View.GONE);
        }
    }

    //проверка заполненности поля "телефон"
    private boolean isValidatePhone(){
        if(edtResetPhone.getText().toString().equals("")){
            Toast.makeText(this, "Заполните телефон!", Toast.LENGTH_SHORT).show();
            return false;
        }
        numberPhone = edtResetPhone.getText().toString();
        return true;
    }

    //проверка заполненности поля "паспорт"
    private boolean isValidatePassport(){
        if(edtResetPassport.getText().toString().equals("")){
            Toast.makeText(this, "Заполните паспорт!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //проверка правильности даты рождения
    private boolean isBirthdayUser(String birthday){
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        int user_id = -1;

        String passport = edtResetPassport.getText().toString();

        if (connection != null) {
            String sqlQueryFindUser = "select [код пациента] from пациент " +
                    "where паспорт='" + passport + "' and " +
                    "[дата рождения]='" + birthday + "'";
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
            if (user_id == -1) { //проверка, есть ли такой пользователь
                Toast.makeText(this, "Неправильная дата рождения!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    private int findPacient() {
        String numberExample1 ="", numberExample2 ="", numberExample3 ="";
        passport = edtResetPassport.getText().toString();

        if(numberPhone.length()>16){
            //различные форматы номера телефона
            //нужны для условия в запросе sql, чтобы смог найти их независимо от формата
            numberExample1="8"+numberPhone.charAt(3)
                    +numberPhone.charAt(4)
                    +numberPhone.charAt(5)
                    +numberPhone.charAt(8)
                    +numberPhone.charAt(9)
                    +numberPhone.charAt(10)
                    +numberPhone.charAt(12)
                    +numberPhone.charAt(13)
                    +numberPhone.charAt(15)
                    +numberPhone.charAt(16)+"";

            numberExample2="+7"+numberPhone.charAt(3)
                    +numberPhone.charAt(4)
                    +numberPhone.charAt(5)
                    +numberPhone.charAt(8)
                    +numberPhone.charAt(9)
                    +numberPhone.charAt(10)
                    +numberPhone.charAt(12)
                    +numberPhone.charAt(13)
                    +numberPhone.charAt(15)
                    +numberPhone.charAt(16)+"";

            numberExample3="7"+numberPhone.charAt(3)
                    +numberPhone.charAt(4)
                    +numberPhone.charAt(5)
                    +numberPhone.charAt(8)
                    +numberPhone.charAt(9)
                    +numberPhone.charAt(10)
                    +numberPhone.charAt(12)
                    +numberPhone.charAt(13)
                    +numberPhone.charAt(15)
                    +numberPhone.charAt(16)+"";
        }

        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();
        int user_id = -1;

        if (connection != null) {
            String sqlQueryFindUser = "select [код пациента] from пациент " +
                    "where телефон='"+numberPhone+"' or  " +
                    "телефон='"+numberExample1+"' or " +
                    "телефон='"+numberExample2+"' or " +
                    "телефон='"+numberExample3+"' or " +
                    "паспорт='"+passport+"'";
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
            if (user_id == -1) { //проверка, есть ли такой пользователь
                Toast.makeText(this, "Такого пользователя не существует!", Toast.LENGTH_SHORT).show();
            }
        }
        return user_id;
    }


    private void alertDialogCheckCode(){
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.layout_check_code, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final MaskEditText userInput = promptsView.findViewById(R.id.edtCheckCode);
        final TextView txtCheckCode = promptsView.findViewById(R.id.txtCheckCode);

        txtCheckCode.setText("Текстовое сообщение с кодом проверки отправлено на \n"+numberPhone+"\n\nВведите код:");

        //Генерация кода подтверждения
        int min = 1000;
        int max = 9999;
        int diff = max - min;
        Random random = new Random();
        int code = random.nextInt(diff + 1);
        code += min;
        Toast.makeText(ResetPasswordActivity.this, code+"", Toast.LENGTH_SHORT).show();

        //Отправка сообщения с кодом
        int finalCode = code;
        String codeString = String.valueOf(finalCode);
        SmsManager.getDefault()
                .sendTextMessage(numberPhone, null, codeString, null, null);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setTitle("Подтверждение телефона")
                .setCancelable(false)
                .setPositiveButton("Подтвердить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(!Objects.requireNonNull(userInput.getText()).toString().equals(codeString)){
                                    Toast.makeText(ResetPasswordActivity.this, "Неправильный код! Повторите попытку", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                else{
                                    Toast.makeText(ResetPasswordActivity.this, "Правильный код!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordActivity2.class);

                                    //поиск пользователя по номеру телефона
                                    int pacient_id = findPacient();
                                    intent.putExtra("pacient_id", pacient_id);
                                    startActivityForResult(intent, 101);
                                }
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
    }

    private void alertDialogCheckPassport(){
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.layout_check_passport, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        txtCheckBirthday = promptsView.findViewById(R.id.txtCheckBirthday);

        setInitialDateTime();

        final ImageButton imgButtonDateCheck = promptsView.findViewById(R.id.imgButtonDateCheck);
        imgButtonDateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBirthday(view);
            }
        });

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setTitle("Подтверждение даты рождения")
                .setCancelable(false)
                .setPositiveButton("Подтвердить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                String selectedDate = dateFormat.format(dateAndTime.getTime());

                                /*
                                SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd", new Locale("ru"));
                                SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));

                                Date dateParse = null;
                                try {
                                    dateParse = formatInput.parse(String.valueOf(dateAndTime.getTime()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                String birthday = formatOutput.format(dateParse);
                                txtCheckBirthday.setText(birthday);
                                dateAndTime.setTime(dateParse); //чтобы при открытии выбора даты, была дата дня рождения*/

                                //проверка, соответствует введенная дата рождения тому, которая в БД
                                if(!isBirthdayUser(selectedDate))
                                    return;

                                Toast.makeText(ResetPasswordActivity.this, "Правильная дата рождения!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordActivity2.class);

                                //поиск пользователя
                                int pacient_id = findPacient();

                                intent.putExtra("pacient_id", pacient_id);
                                startActivityForResult(intent, 101);
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
    }

    private void setInitialDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        String currentTime = dateFormat.format(dateAndTime.getTime());
        txtCheckBirthday.setText(currentTime);
    }

    public void setBirthday(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && data!=null){
            setResult(RESULT_OK, getIntent());
            finish();
        }

    }
}
