package com.example.api; // Update with your actual package name

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API extends AppCompatActivity {

    // UI elements
    private TextView stockPriceTextView, exchangeTextView, companyText;
    private Spinner symbolSpinner; // Dropdown for stock symbols
    private Button updateButton;
    private double previousPrice = 0.0;
    private static final String API_KEY = String.valueOf(R.string.api_key); // Your Alpha Vantage API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api); // Ensure you have this layout file

        // Bind UI elements
        stockPriceTextView = findViewById(R.id.stockPriceTextView);
        exchangeTextView = findViewById(R.id.highestPriceTextView);
        companyText = findViewById(R.id.openPriceTextView);
        updateButton = findViewById(R.id.updateButton);
        symbolSpinner = findViewById(R.id.symbolSpinner); // Initialize the Spinner

        // Set up button click listener
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected item from the Spinner
                String symbolString = symbolSpinner.getSelectedItem().toString();
                if (!symbolString.isEmpty()) {
                    fetchStockPrice(symbolString); // Fetch the stock price
                } else {
                    Toast.makeText(API.this, "Please select a valid symbol", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Fetch stock price from the Alpha Vantage API using OkHttp
    private void fetchStockPrice(String symbolString) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
                + symbolString + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(API.this, "Failed to fetch stock data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

                    // Check if the response contains time series data
                    if (jsonObject.has("Time Series (Daily)")) {
                        JsonObject timeSeries = jsonObject.getAsJsonObject("Time Series (Daily)");

                        // Get the first (latest) date in the Time Series
                        Iterator<String> datesIterator = timeSeries.keySet().iterator();
                        if (datesIterator.hasNext()) {
                            String latestDate = datesIterator.next(); // Get the latest date
                            JsonObject latestData = timeSeries.getAsJsonObject(latestDate);

                            double openPrice = latestData.get("1. open").getAsDouble();
                            double highPrice = latestData.get("2. high").getAsDouble();
                            double closePrice = latestData.get("4. close").getAsDouble();

                            // Update the UI with the fetched stock data
                            runOnUiThread(() -> updateStockPrice(closePrice, openPrice, highPrice));
                        } else {
                            runOnUiThread(() -> Toast.makeText(API.this, "No stock data available", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(API.this, "Invalid response from API", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(API.this, "Failed to fetch stock data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Update the UI with the fetched stock data
    private void updateStockPrice(double currentPrice, double openPrice, double highPrice) {
        double priceChange = currentPrice - previousPrice;
        previousPrice = currentPrice;

        // Update UI on the main thread
        runOnUiThread(() -> {
            // Format and display the current price
            String formattedPrice = String.format("%.2f", currentPrice);
            stockPriceTextView.setText(formattedPrice);

            // Change text color based on price movement
            int textColor = (priceChange >= 0.00) ? Color.GREEN : Color.RED;
            stockPriceTextView.setTextColor(textColor);

            // Update exchangeTextView and companyText with high price and open price
            exchangeTextView.setText("High Price: " + highPrice);
            companyText.setText("Open Price: " + openPrice);
        });
    }
}
