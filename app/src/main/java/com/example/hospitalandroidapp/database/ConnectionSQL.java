package com.example.hospitalandroidapp.database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionSQL {
    Connection connection;

    @SuppressLint("New Api")
    public Connection connectionClass(){
        String ip="192.168.43.57", port="1433", db="Больница", username="sa2", password="12345";

        StrictMode.ThreadPolicy threadPolicy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(threadPolicy);
        String connectURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectURL = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+db;
            connection = DriverManager.getConnection(connectURL, username, password);
        }
        catch (Exception e){
            Log.e("Error is ", e.getMessage());
        }
        return connection;
    }
}
