package com.example.hospitalandroidapp.database;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSQL {
    Connection connection;

    @SuppressLint("New Api")
    public Connection connectionClass(){
        String ip="192.168.0.174", port="1433", db="Больница", username="sa2", password="12345";

        StrictMode.ThreadPolicy threadPolicy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(threadPolicy);
        String connectURL = null;
        connection = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectURL = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+db;

            String finalConnectURL = connectURL;
            //connection = DriverManager.getConnection(finalConnectURL, username, password);

            if(isConnectionValid(DriverManager.getConnection(finalConnectURL, username, password)))
                connection = DriverManager.getConnection(finalConnectURL, username, password);
            else
                connection = null;

        }
        catch (Exception e){
            Log.e("Error is ", e.getMessage());
        }
        return connection;
    }
    public static boolean isConnectionValid(Connection connection)
    {
        try {
            if (connection != null && !connection.isClosed()) {
                // Running a simple validation query
                connection.prepareStatement("SELECT 1");
                return true;
            }
        }
        catch (SQLException e) {
            // log some useful data here
        }
        return false;
    }

    public static Boolean isValid(Connection connection)
            throws SQLException
    {
        if (connection.isValid(5)) {
            return  true;
        }
        return false;
    }
}
