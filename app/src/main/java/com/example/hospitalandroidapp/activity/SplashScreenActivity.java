package com.example.hospitalandroidapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hospitalandroidapp.MainActivity;
import com.example.hospitalandroidapp.R;
import com.example.hospitalandroidapp.database.ConnectionSQL;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imgSplashScreen = findViewById(R.id.imgSplashScreen);
        Glide.with(this).load(R.drawable.splash_gif).into(imgSplashScreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                /*
                ConnectionSQL connectionSQL = new ConnectionSQL();
                Connection connection = connectionSQL.connectionClass();
                try {
                    if(connectionSQL.isValid(connection))
                        Toast.makeText(context, "efse", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "FFFFGG", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }*/

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
                int pacient_id = sharedPref.getInt(getString(R.string.pref_id), 0);

                if(pacient_id!=0){
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }

            }
        }, 5000);
    }
}