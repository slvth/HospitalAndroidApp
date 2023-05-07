package com.example.hospitalandroidapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.example.hospitalandroidapp.database.PacientModel;
import com.example.hospitalandroidapp.database.RecordReceptionModel;
import com.santalu.maskara.widget.MaskEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class AccountFragment2 extends Fragment {
    Connection connection;
    PacientModel pacient = new PacientModel();
    LinearLayout linearMainButtons, linearEditButtons, linearAccount;
    TextView txtAccountFIO, txtAccountID, txtAccountBirthday;
    MaskEditText edtAccountPhone,edtAccountPassport,edtAccountPolicyOMS,edtAccountSNILS;
    EditText edtAddressRegistration, edtAddressResidence;
    RadioButton rbtnAccountMale, rbtnAccountFemale;
    Button btnOpenHistory, btnEditAccount, btnAccountCancel, btnAccountSave;
    ImageButton btnAccountDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account2, container, false);

        linearMainButtons = v.findViewById(R.id.linearMainButtons);
        linearEditButtons = v.findViewById(R.id.linearEditButtons);
        linearAccount = v.findViewById(R.id.linearAccount);

        txtAccountFIO = v.findViewById(R.id.txtAccountFIO);
        txtAccountID = v.findViewById(R.id.txtAccountID);
        txtAccountBirthday = v.findViewById(R.id.txtAccountBirthday);

        edtAccountPhone = v.findViewById(R.id.edtAccountPhone);
        edtAccountPassport = v.findViewById(R.id.edtAccountPassport);
        edtAccountPolicyOMS = v.findViewById(R.id.edtAccountPolicyOMS);
        edtAccountSNILS = v.findViewById(R.id.edtAccountSNILS);
        edtAddressRegistration = v.findViewById(R.id.edtAddressRegistration);
        edtAddressResidence = v.findViewById(R.id.edtAddressResidence);

        rbtnAccountMale = v.findViewById(R.id.rbtnAccountMale);
        rbtnAccountFemale = v.findViewById(R.id.rbtnAccountFemale);

        btnOpenHistory = v.findViewById(R.id.btnOpenHistory);
        btnEditAccount = v.findViewById(R.id.btnEditAccount);
        btnAccountCancel = v.findViewById(R.id.btnAccountCancel);
        btnAccountSave = v.findViewById(R.id.btnAccountSave);
        btnAccountDate = v.findViewById(R.id.btnAccountDate);

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAccount();
            }
        });

        btnAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enabledAll(false);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enabledAll(false);
        downloadData();
        setData();
    }

    private void editAccount(){
        enabledAll(true);
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

        Transition transition = new Fade();
        transition.setDuration(600);
        transition.addTarget(R.id.linearMainButtons);

        Transition transition2 = new Fade();
        transition2.setDuration(600);
        transition2.addTarget(R.id.linearEditButtons);

        if(isEdit){
            TransitionManager.beginDelayedTransition(linearAccount, transition2);
            linearEditButtons.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(linearAccount, transition);
            linearMainButtons.setVisibility(View.GONE);
        }
        else{
            TransitionManager.beginDelayedTransition(linearAccount, transition);
            linearMainButtons.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(linearAccount, transition2);
            linearEditButtons.setVisibility(View.GONE);
        }
    }

    private void setData(){
        txtAccountFIO.setText(pacient.getFio());
        txtAccountID.setText(String.valueOf(pacient.getPacient_id()));

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
}
