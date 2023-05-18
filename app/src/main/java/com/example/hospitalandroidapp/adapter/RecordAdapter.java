package com.example.hospitalandroidapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalandroidapp.MainActivity;
import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.activity.ResetPasswordActivity;
import com.example.hospitalandroidapp.activity.ResetPasswordActivity2;
import com.example.hospitalandroidapp.database.ConnectionSQL;
import com.example.hospitalandroidapp.database.RecordReceptionModel;
import com.example.hospitalandroidapp.fragment.RecordFragment;

import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    Connection connection;
    Context context;
    ArrayList<RecordReceptionModel> records;
    Boolean isRecordFragment;


    public RecordAdapter(Context context, ArrayList<RecordReceptionModel> records, Boolean isRecordFragment) {
        this.context = context;
        this.records = records;
        this.isRecordFragment = isRecordFragment;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("ru"));
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
        String dateInString = records.get(position).getDate();
        Date dateParse = null;
        try {
            dateParse = formatInput.parse(dateInString);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String date = formatOutput.format(dateParse);
        String doctor = "Врач: "+records.get(position).getDoctorFIO();

        holder.txtDateItemRecord.setText(date);
        holder.txtDoctorItemRecord.setText(doctor);

        holder.btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

                if(isRecordFragment){
                    //Настраиваем сообщение в диалоговом окне:
                    mDialogBuilder
                            .setTitle("Отмена записи")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Вы точно хотите отменить запись?Запись: \n"+date+"\n"+doctor)
                            .setCancelable(false)
                            .setPositiveButton("Подтвердить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            ConnectionSQL connectionSQL = new ConnectionSQL();
                                            connection = connectionSQL.connectionClass();

                                            if(connection != null) {
                                                Statement statement = null;
                                                String sqlQuery = "DELETE FROM [запись на прием] WHERE [код записи на прием]="+records.get(holder.getAdapterPosition()).getRecord_id();
                                                try {
                                                    statement = connection.createStatement();
                                                    statement.executeQuery(sqlQuery);
                                                    connection.close();
                                                } catch (Exception e) {
                                                    Log.e("Error: ", e.getMessage());
                                                }

                                                //обновление списка
                                                int actualPosition = holder.getAdapterPosition();
                                                records.remove(actualPosition);
                                                notifyItemRemoved(actualPosition);
                                                notifyItemRangeChanged(actualPosition, records.size());
                                            }
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                }
                else{
                    //Настраиваем сообщение в диалоговом окне:
                    mDialogBuilder
                            .setTitle("Удаление записи")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Вы точно хотите удалить запись? Запись: \n"+date+"\n"+doctor)
                            .setCancelable(false)
                            .setPositiveButton("Подтвердить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            ConnectionSQL connectionSQL = new ConnectionSQL();
                                            connection = connectionSQL.connectionClass();

                                            if(connection != null) {
                                                Statement statement = null;
                                                String sqlQuery = "DELETE FROM [запись на прием] WHERE [код записи на прием]="+records.get(holder.getAdapterPosition()).getRecord_id();
                                                try {
                                                    statement = connection.createStatement();
                                                    statement.executeQuery(sqlQuery);
                                                    connection.close();
                                                } catch (Exception e) {
                                                    Log.e("Error: ", e.getMessage());
                                                }

                                                //обновление списка
                                                int actualPosition = holder.getAdapterPosition();
                                                records.remove(actualPosition);
                                                notifyItemRemoved(actualPosition);
                                                notifyItemRangeChanged(actualPosition, records.size());
                                            }
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                }
                //Создаем AlertDialog:
                AlertDialog alertDialog = mDialogBuilder.create();
                //и отображаем его:
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}


