package com.example.hospitalandroidapp.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.activity.HistoryRecordActivity;
import com.example.hospitalandroidapp.activity.LoginActivity;
import com.example.hospitalandroidapp.adapter.RecordAdapter;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.example.hospitalandroidapp.database.PacientModel;
import com.example.hospitalandroidapp.database.RecordReceptionModel;
import com.santalu.maskara.widget.MaskEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class AccountFragment2 extends Fragment {
    Connection connection;
    PacientModel pacient = new PacientModel();
    LinearLayout linearMainButtons, linearEditButtons, linearAccount, linearFIO;
    TextView txtAccountFIO, txtAccountID, txtAccountBirthday;
    MaskEditText edtAccountPhone,edtAccountPassport,edtAccountPolicyOMS,edtAccountSNILS;
    EditText edtAccountSurname, edtAccountName, edtAccountMiddleName,  edtAddressRegistration, edtAddressResidence;
    RadioButton rbtnAccountMale, rbtnAccountFemale;
    Button btnExitAccount, btnOpenHistory, btnEditAccount, btnAccountCancel, btnAccountSave;
    ImageButton btnAccountDate;

    Calendar dateAndTime= Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account2, container, false);

        linearMainButtons = v.findViewById(R.id.linearMainButtons);
        linearEditButtons = v.findViewById(R.id.linearEditButtons);
        linearAccount = v.findViewById(R.id.linearAccount);
        linearFIO = v.findViewById(R.id.linearFIO);

        txtAccountFIO = v.findViewById(R.id.txtAccountFIO);
        txtAccountID = v.findViewById(R.id.txtAccountID);
        txtAccountBirthday = v.findViewById(R.id.txtAccountBirthday);

        edtAccountPhone = v.findViewById(R.id.edtAccountPhone);
        edtAccountPassport = v.findViewById(R.id.edtAccountPassport);
        edtAccountPolicyOMS = v.findViewById(R.id.edtAccountPolicyOMS);
        edtAccountSNILS = v.findViewById(R.id.edtAccountSNILS);
        edtAddressRegistration = v.findViewById(R.id.edtAddressRegistration);
        edtAddressResidence = v.findViewById(R.id.edtAddressResidence);
        edtAccountSurname = v.findViewById(R.id.edtAccountSurname);
        edtAccountName = v.findViewById(R.id.edtAccountName);
        edtAccountMiddleName = v.findViewById(R.id.edtAccountMiddleName);

        rbtnAccountMale = v.findViewById(R.id.rbtnAccountMale);
        rbtnAccountFemale = v.findViewById(R.id.rbtnAccountFemale);

        btnExitAccount = v.findViewById(R.id.btnExitAccount);
        btnOpenHistory = v.findViewById(R.id.btnOpenHistory);
        btnEditAccount = v.findViewById(R.id.btnEditAccount);
        btnAccountCancel = v.findViewById(R.id.btnAccountCancel);
        btnAccountSave = v.findViewById(R.id.btnAccountSave);
        btnAccountDate = v.findViewById(R.id.btnAccountDate);

        btnExitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                sharedPref.edit().remove(getString(R.string.pref_id)).commit();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        });
        btnOpenHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryRecordActivity.class);
                startActivity(intent);
            }
        });
        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //режим редактирования включен
                enabledAll(true);
            }
        });

        btnAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enabledAll(false);
            }
        });

        btnAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEdit();
            }
        });

        btnAccountDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBirthday(v);
            }
        });



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //режим редактирования отключен
        enabledAll(false);

        //загрузка данных из бд
        downloadData();

        //загрузка данных в View
        setDataInView();
    }

    private void saveEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Предупреждение!");
        builder.setMessage("Вы точно хотите сохранить изменения?");
        builder.setCancelable(true);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() { // Кнопка Да
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConnectionSQL connectionSQL = new ConnectionSQL();
                connection = connectionSQL.connectionClass();

                if(connection != null){
                    Statement statement = null;

                    String surname, name, middleName, gender, date, phone, passport,
                            policyOMS, SNILS, addressRegistration, addressResidence;

                    surname = edtAccountSurname.getText().toString();
                    name = edtAccountName.getText().toString();
                    middleName = edtAccountMiddleName.getText().toString();
                    gender = rbtnAccountMale.isChecked() ? "мужской" : "женский";
                    phone = edtAccountPhone.getText().toString();
                    passport = edtAccountPassport.getText().toString();
                    policyOMS = edtAccountPolicyOMS.getText().toString();
                    SNILS = edtAccountSNILS.getText().toString();
                    addressRegistration = edtAddressRegistration.getText().toString();
                    addressResidence = edtAddressResidence.getText().toString();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String selectedDate = dateFormat.format(dateAndTime.getTime());

                    String sqlQuery = "UPDATE пациент " +
                            "SET фамилия='"+surname+"', " +
                            "имя='"+name+"', " +
                            "отчество='"+middleName+"', " +
                            "пол='"+gender+"', " +
                            "[дата рождения]='" +selectedDate+"', " +
                            "телефон='"+phone+"', " +
                            "паспорт='"+passport+"', " +
                            "[полис ОМС]='"+policyOMS+"', " +
                            "СНИЛС='"+SNILS+"', " +
                            "[адрес регистрации]='"+addressRegistration+"', " +
                            "[адрес постоянного места жительства]='"+addressResidence+"' " +
                            "WHERE [код пациента]="+pacient.getPacient_id();
                    try {
                        statement = connection.createStatement();
                        statement.executeQuery(sqlQuery);
                        connection.close();

                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                }
                Toast.makeText(getActivity(), "Успешное сохранение!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                downloadData();
                setDataInView();
                enabledAll(false);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() { // Кнопка Нет
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Отпускает диалоговое окно
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enabledAll(Boolean isEdit){
        btnAccountDate.setEnabled(isEdit);
        rbtnAccountMale.setEnabled(isEdit);
        rbtnAccountFemale.setEnabled(isEdit);
        edtAccountPhone.setEnabled(isEdit);
        edtAccountPassport.setEnabled(isEdit);
        edtAccountPolicyOMS.setEnabled(isEdit);
        edtAccountSNILS.setEnabled(isEdit);
        edtAddressRegistration.setEnabled(isEdit);
        edtAddressResidence.setEnabled(isEdit);

        //Transition transition = new Slide(Gravity.TOP);
        Transition transition = new Fade();
        transition.setDuration(500);
        transition.addTarget(R.id.linearMainButtons);

        //Transition transition2 = new Slide(Gravity.TOP);
        Transition transition2 = new Fade();
        transition2.setDuration(500);
        transition2.addTarget(R.id.linearEditButtons);

        if(isEdit){
            TransitionManager.beginDelayedTransition(linearAccount, transition2);
            linearEditButtons.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(linearAccount, transition);
            linearMainButtons.setVisibility(View.GONE);

            linearFIO.setVisibility(View.VISIBLE);
        }
        else{
            TransitionManager.beginDelayedTransition(linearAccount, transition);
            linearMainButtons.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(linearAccount, transition2);
            linearEditButtons.setVisibility(View.GONE);

            linearFIO.setVisibility(View.GONE);
        }
    }

    private void setDataInView(){
        txtAccountFIO.setText(pacient.getFio());
        txtAccountID.setText(String.valueOf(pacient.getPacient_id()));

        edtAccountSurname.setText(pacient.getSurname());
        edtAccountName.setText(pacient.getName());
        edtAccountMiddleName.setText(pacient.getMiddleName());
        edtAccountPhone.setText(pacient.getPhone());
        edtAccountPassport.setText(pacient.getPassport());
        edtAccountPolicyOMS.setText(pacient.getPolicyOMS());
        edtAccountSNILS.setText(pacient.getSNILS());

        edtAddressRegistration.setText(pacient.getAddressRegistration());
        edtAddressResidence.setText(pacient.getAddressResidence());

        if(pacient.getGender().equals("мужской"))
            rbtnAccountMale.setChecked(true);
        else
            rbtnAccountFemale.setChecked(true);

        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd", new Locale("ru"));
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        Date dateParse = null;
        try {
            dateParse = formatInput.parse(pacient.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String birthday = formatOutput.format(dateParse);
        txtAccountBirthday.setText(birthday);

        dateAndTime.setTime(dateParse); //чтобы при открытии выбора даты, была дата дня рождения
    }

    private void downloadData(){
        ConnectionSQL connectionSQL = new ConnectionSQL();
        connection = connectionSQL.connectionClass();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        int pacient_id = sharedPref.getInt(getString(R.string.pref_id), 0);

        if(connection != null){
            String sqlQuery = "select [фамилия],[имя],[отчество],\n" +
                    "[пол],[телефон],[дата рождения],\n" +
                    "[паспорт],[полис ОМС], [СНИЛС],\n" +
                    "[адрес регистрации],\n" +
                    "[адрес постоянного места жительства]\n" +
                    "from пациент where [код пациента] = "+pacient_id;

            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet set = statement.executeQuery(sqlQuery);
                while (set.next()){
                    String surname = set.getString(1);
                    String name = set.getString(2);
                    String middleName = set.getString(3);
                    String gender = set.getString(4);
                    String phone = set.getString(5);
                    String date = set.getString(6);
                    String passport = set.getString(7);
                    String policyOMS = set.getString(8);
                    String SNILS = set.getString(9);
                    String addressRegistration = set.getString(10);
                    String addressResidence = set.getString(11);
                    pacient = new PacientModel(pacient_id, surname, name, middleName, gender, date,
                            phone,passport,policyOMS,SNILS,addressRegistration,addressResidence);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
    }

    // установка даты рождения
    private void setInitialDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        String currentTime = dateFormat.format(dateAndTime.getTime());
        txtAccountBirthday.setText(currentTime);
    }

    public void setBirthday(View v) {
        new DatePickerDialog(getContext(), d,
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

}
