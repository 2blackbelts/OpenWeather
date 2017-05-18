package com.tafe.mcintosh.openweather;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    Typeface weatherFont;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    Handler handler;

    public WeatherActivity(){
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");

        updateWeatherData(new CityPreference(this).getCity());
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }
        return false;
    }
    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

//    public void changeCity(String city){
////        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
////                .findFragmentById(R.id.container);
////        wf.changeCity(city);
//
//        new CityPreference(this).setCity(city);
//    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(WeatherActivity.this, city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(WeatherActivity.this,
                                    Resources.getSystem().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            Log.e("json", json.getJSONObject("sys").toString());
            Log.e("json", json.getJSONObject("main").getString("temp"));
            currentTemperatureField.setText(json.getJSONObject("main").getString("temp")+" ℃");
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));
//
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");
                    
                    //http://api.openweathermap.org/data/2.5/weather?q=sydney&units=metric&appid=9f5bae41fceccaf4ff79849fb5455faf
//                    Log.e("json", json.getJSONObject("main").getString("temp"));

//            DateFormat df = DateFormat.getDateTimeInstance();
//            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
//            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data" + e);
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
                //TODO get hour of day from Calendar class...
//            long currentTime = Calendar.get(Calendar.HOUR_OF_DAY);
//            if(currentTime>=sunrise && currentTime<sunset) {
                icon = WeatherActivity.this.getString(R.string.weather_sunny);
//            } else {
//                icon = WeatherActivity.this.getString(R.string.weather_clear_night);
//            }
        } else {
            switch(id) {
                case 2 : icon = WeatherActivity.this.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = WeatherActivity.this.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = WeatherActivity.this.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = WeatherActivity.this.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = WeatherActivity.this.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = WeatherActivity.this.getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }

}
