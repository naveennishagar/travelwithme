package com.tugaybakay.travelpartner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class weatherActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        private static final String OPENWEATHERMAP_API_KEY = "http://api.openweathermap.org/data/2.5/weather?q=";
        private static final String OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(OPENWEATHERMAP_API_URL + params[0] + "&appid=" + OPENWEATHERMAP_API_KEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                result = stringBuilder.toString();
            } catch (Exception e) {
                Log.e("WeatherApp", "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String description = weatherObject.getString("description");
                weatherTextView.setText(description);
            } catch (JSONException e) {
                Log.e("WeatherApp", "Error parsing JSON", e);
            }
        }
    }

    // Call this method when the "Get Weather" button is clicked
    public void onGetWeatherClicked() {
        String city = cityEditText.getText().toString();
        new FetchWeatherTask().execute(city);

        Intent intent = new Intent(weatherActivity.this, AddActivity.class);
        startActivity(intent);
        finish();
    }
}