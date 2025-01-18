package com.example.api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStockPrice = findViewById(R.id.buttonStockPrice);
        ImageButton buttonLearn = findViewById(R.id.buttonLearn);
        Button buttonOption = findViewById(R.id.buttonOther1);
        Button buttonOther2 = findViewById(R.id.buttonOther2);

        // Set up button click listeners
        buttonLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SharpeRatio.class);
                startActivity(intent);
            }
        });

        buttonStockPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, API.class);
                startActivity(i); // Start the new activity
            }
        });

        buttonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(MainActivity.this, Options.class);
                startActivity(i1); // Start the new activity
            }
        });
}
}
