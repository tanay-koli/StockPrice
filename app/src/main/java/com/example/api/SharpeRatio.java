package com.example.api; // Update with your actual package name

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SharpeRatio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharpe_ratio); // Set the content view to your layout
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optionally finish the activity if you want to remove it from the back stack
        finish();
    }
}
