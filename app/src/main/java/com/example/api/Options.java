package com.example.api;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Options extends AppCompatActivity {

    private static final String TAG = "OptionsActivity";
    private Spinner symbolSpinner, spRole;
    private Button btnConfirm, btnGetMidValue, btnCalculate;
    private TextView midValueTextView, tvResult;
    private EditText etStrikePrice, etExpiry, etPremium, etLotSize;
    private String selectedSymbol;
    private Double midValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Initialize Views
        symbolSpinner = findViewById(R.id.symbolSpinner);
        spRole = findViewById(R.id.spRole); // Initialize spRole here
        btnConfirm = findViewById(R.id.btnConfirm);
        btnGetMidValue = findViewById(R.id.btnGetMidValue);
        midValueTextView = findViewById(R.id.midValueTextView);
        etStrikePrice = findViewById(R.id.etStrikePrice);
        etExpiry = findViewById(R.id.etExpiry);
        etPremium = findViewById(R.id.etPremium);
        etLotSize = findViewById(R.id.etLotSize);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        // Populate stock symbols from strings.xml
        String[] stockSymbols = getResources().getStringArray(R.array.stock_symbols);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stockSymbols);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symbolSpinner.setAdapter(adapter);

        // Populate the spRole spinner with "Call Buy" and "Put Buy"
        String[] roles = {"Call Buy (CB)", "Put Buy (PB)"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        // Set a listener for the symbol spinner to get the selected symbol
        symbolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSymbol = stockSymbols[position]; // Store the selected symbol
                Log.d(TAG, "Selected symbol: " + selectedSymbol);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up button click listener for Confirm button
        btnConfirm.setOnClickListener(v -> {
            // Show the additional fields and buttons after confirmation
            btnGetMidValue.setVisibility(View.VISIBLE);
            midValueTextView.setVisibility(View.VISIBLE);
            etStrikePrice.setVisibility(View.VISIBLE);
            etExpiry.setVisibility(View.VISIBLE);
            etPremium.setVisibility(View.VISIBLE);
            etLotSize.setVisibility(View.VISIBLE);
            btnCalculate.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.VISIBLE);
            spRole.setVisibility(View.VISIBLE); // Ensure spRole is visible
        });

        // Set up button click listener to get mid value
        btnGetMidValue.setOnClickListener(v -> {
            if (selectedSymbol != null) {
                fetchMidValue(selectedSymbol); // Fetch mid value when button is clicked
            } else {
                midValueTextView.setText("Please select a stock symbol");
            }
        });

        // Set up button click listener for calculating expected return
        btnCalculate.setOnClickListener(v -> calculateExpectedReturn());
    }

    private void fetchMidValue(String symbol) {

        String url = "https://api.marketdata.app/v1/stocks/quotes/" + symbol + "/?token=TWxVM1RyaVo0Uks2QzBpZnQyU09qdm1JTmJqMks1UUswd0ZERkI4SS1Dbz0"; // Replace with your valid token
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network call failed", e);
                runOnUiThread(() -> midValueTextView.setText("Error fetching data"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response received: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        if (jsonObject.getString("s").equals("ok")) {
                            JSONArray midArray = jsonObject.getJSONArray("last");
                            midValue = midArray.getDouble(0); // Get the first value of the mid array
                            runOnUiThread(() -> midValueTextView.setText("Spot Value: " + midValue));
                        } else {
                            runOnUiThread(() -> midValueTextView.setText("Invalid response"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON", e);
                        runOnUiThread(() -> midValueTextView.setText("Error parsing data"));
                    }
                } else {
                    Log.e(TAG, "Request not successful: " + response.code());
                    runOnUiThread(() -> midValueTextView.setText("Request not successful"));
                }
            }
        });
    }

    private void calculateExpectedReturn() {
        // Retrieve input values
        String strikePriceStr = etStrikePrice.getText().toString();
        String expiryStr = etExpiry.getText().toString();
        String premiumStr = etPremium.getText().toString();
        String lotSizeStr = etLotSize.getText().toString();
        String role = spRole.getSelectedItem().toString();

        // Validate inputs
        if (TextUtils.isEmpty(strikePriceStr) || TextUtils.isEmpty(expiryStr) ||
                TextUtils.isEmpty(premiumStr) || TextUtils.isEmpty(lotSizeStr)) {
            Toast.makeText(Options.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double strikePrice = Double.parseDouble(strikePriceStr);
        int expiry = Integer.parseInt(expiryStr);
        double premium = Double.parseDouble(premiumStr);
        int lotSize = Integer.parseInt(lotSizeStr);

        // Calculate expected return
        double expectedReturn = 0;
        if (role.equals("Call Buy (CB)")) {
            expectedReturn = (Math.max(0, midValue - strikePrice) * lotSize) - (premium * lotSize);
        } else if (role.equals("Put Buy (PB)")) {
            expectedReturn = (Math.max(0, strikePrice - midValue) * lotSize) - (premium * lotSize);
        }

        // Display result
        tvResult.setText(String.format("Result: %.2f", expectedReturn));
    }
}
