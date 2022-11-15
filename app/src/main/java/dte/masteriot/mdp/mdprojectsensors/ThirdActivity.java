package dte.masteriot.mdp.mdprojectsensors;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdActivity extends AppCompatActivity {
    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT"; // to easily filter logs
    private String logTag; // to clearly identify logs
    private static final String URL_API = "https://api.open-meteo.com/v1/forecast?latitude=51.5002&longitude=-0.1262&hourly=temperature_2m,relativehumidity_2m,cloudcover&timezone=Europe%2FLondon&start_date=2022-11-01&end_date=2022-11-01";
    private static final String URL_API1 ="https://api.open-meteo.com/v1/forecast?";
    private static final String LAT ="latitude=";
    private static final String LONG="&longitude=";
    private static final String PAR= "&hourly=temperature_2m,relativehumidity_2m,cloudcover&timezone=Europe%2FLondon&";
    private static final String D_START="start_date=";
    private static final String D_END = "&end_date=";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private String lat, lon;
    private String time_now,temp_now,hum_now,cloud_now, Light, Temperature, Humidity, Date, Name = "- - -";
    private TextView textTemp_api,textHum_api,textCloud_api,textTemp_mqtt,textHum_mqtt,textCloud_mqtt, textName, textEnergy, textDesv;
    ExecutorService es;
    private Integer hour_index;
    private Integer optimalVal;
    private double energy, desv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Build the logTag with the Thread and Class names:
        logTag = LOGSLOADWEBCONTENT + ", Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        // Get references to UI elements:
        textTemp_api = findViewById(R.id.temp_api);
        textHum_api = findViewById(R.id.hum_api);
        textCloud_api = findViewById(R.id.cloud_api);
        textTemp_mqtt = findViewById(R.id.temp_text);
        textHum_mqtt = findViewById(R.id.hum_text);
        textCloud_mqtt = findViewById(R.id.cloud_text);
        textName = findViewById(R.id.name_gh);
        textEnergy = findViewById(R.id.text_energy);
        textDesv = findViewById(R.id.text_desv);
        // Get measures and time from subscriber
        Intent inputIntent = getIntent();
        Light = inputIntent.getStringExtra("Light");
        Humidity = inputIntent.getStringExtra("Humidity");
        Temperature = inputIntent.getStringExtra("Temperature");
        Date = inputIntent.getStringExtra("Date");
        Name = inputIntent.getStringExtra("Name");
        textName.setText(Name.toUpperCase(Locale.ROOT)+" RECORD");
        //String []part=Date.split(" ");
        //String [] full_time = part[1].split(":");
        /*try{
            //for getting all measures in correct index
            hour_index = Integer.parseInt(full_time[0]);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }*/
        //String date_2_api=part[0]:
        //String full_URL = URL_API1+LAT+lat+LONG+lon+PAR+D_START+date_2_api+D_END+date_2_api;
        switch (Name){
            case "Tomato":
                lat= getString(R.string.tomato_lat);
                lon = getString(R.string.tomato_long);
                optimalVal = getResources().getInteger(R.integer.Tomato);
                break;
            case "Pepper":
                lat= getString(R.string.pepper_lat);
                lon = getString(R.string.pepper_long);
                optimalVal = getResources().getInteger(R.integer.Pepper);
                break;
            case "Eggplant":
                lat= getString(R.string.egg_lat);
                lon = getString(R.string.egg_long);
                optimalVal = getResources().getInteger(R.integer.Eggplant);
                break;
            case "Watermelon":
                lat= getString(R.string.wat_lat);
                lon = getString(R.string.wat_long);
                optimalVal = getResources().getInteger(R.integer.Watermelon);
                break;
            case "Zucchini":
                lat= getString(R.string.zuc_lat);
                lon = getString(R.string.zuc_long);
                optimalVal = getResources().getInteger(R.integer.Zucchini);
                break;
            case "Cucumber":
                lat= getString(R.string.cuc_lat);
                lon = getString(R.string.cuc_long);
                optimalVal = getResources().getInteger(R.integer.Cucumber);
                break;
            case "Melon":
                lat= getString(R.string.melon_lat);
                lon = getString(R.string.melon_long);
                optimalVal = getResources().getInteger(R.integer.Melon);
                break;
            default:
                lat= getString(R.string.bean_lat);
                lon = getString(R.string.bean_long);
                optimalVal = getResources().getInteger(R.integer.Green_bean);
                break;
        }
        String full_URL = URL_API1+LAT+lat+LONG+lon+PAR+D_START+"2022-11-01"+D_END+"2022-11-01";
        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, full_URL);
        es.execute(loadURLContents);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent inputIntent = getIntent();
        Light = inputIntent.getStringExtra("Light");
        Humidity = inputIntent.getStringExtra("Humidity");
        Temperature = inputIntent.getStringExtra("Temperature");
        Date = inputIntent.getStringExtra("Date");
        Name = inputIntent.getStringExtra("Name");
        textName.setText(Name.toUpperCase(Locale.ROOT)+" RECORD");
        //from mqtt
        if(Temperature != null){
            textTemp_mqtt.setText(Temperature + "ºC");
        } else {
            textTemp_mqtt.setText("No data");
        }

        if(Humidity != null) {
            textHum_mqtt.setText(Humidity + "%");
        } else {
            textHum_mqtt.setText("No data");
        }

        if( Light != null) {
            textCloud_mqtt.setText(Light);
        } else {
            textCloud_mqtt.setText("No data");
        }
    }


    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;

            super.handleMessage(msg);
            Log.d(logTag, "message received from background thread");
            if((string_result = msg.getData().getString("text")) != null) {
                try {
                    JSONObject json_obj = new JSONObject(string_result);
                    JSONObject hourly = json_obj.getJSONObject("hourly");
                    JSONArray time = hourly.getJSONArray("time");
                    JSONArray temperature = hourly.getJSONArray("temperature_2m");
                    JSONArray humidity = hourly.getJSONArray("relativehumidity_2m");
                    JSONArray cloudcover = hourly.getJSONArray("cloudcover");
                    List<String> timeArray=new ArrayList<String>();
                    List<String> tempArray=new ArrayList<String>();
                    List<String> humArray=new ArrayList<String>();
                    List<String> cloudArray=new ArrayList<String>();
                    for (int r = 0; r < time.length(); r++) {
                        timeArray.add(time.getString(r));
                        tempArray.add(temperature.getString(r));
                        humArray.add(humidity.getString(r));
                        cloudArray.add(cloudcover.getString(r));
                    }
                    LocalDateTime now = java.time.LocalDateTime.now();
                    Integer hour = now.toLocalTime().getHour();

                    time_now  = timeArray.get(hour);
                    temp_now  = tempArray.get(hour);
                    hum_now   = humArray.get(hour);
                    cloud_now = cloudArray.get(hour);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (Temperature == null) {
                        energy = 0;
                        desv = 0;
                    } else {
                        Double tempInt = Double.parseDouble(Temperature);
                        Double tempExt = Double.parseDouble(temp_now.toString());
                        desv = (Math.abs(tempInt - optimalVal) / optimalVal) * 100;
                        Double absEnergy = Math.abs(tempInt - tempExt);
                        if (absEnergy <= 5) {
                            energy = 10 * absEnergy;
                        } else {
                            if (absEnergy <= 10) {
                                energy = 11 * absEnergy - 5;
                            } else {
                                if (absEnergy <= 15) {
                                    energy = 3.2 * absEnergy + 73;
                                } else {
                                    energy = 3.2 * absEnergy + 50;
                                }
                            }
                        }
                    }
                    }catch(NumberFormatException ex){
                        ex.printStackTrace();


                }

                //from weather api
                textTemp_api.setText(temp_now+"ºC");
                textHum_api.setText(hum_now+"%");
                textCloud_api.setText(cloud_now+"%");


                //formulas
                NumberFormat formatter = new DecimalFormat("0.00");
                textEnergy.setText(formatter.format(energy)+" W/m2");
                textDesv.setText(formatter.format(desv)+"%");
            }
        }
    };


    public void readJSON(View view) {
        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_API);
        es.execute(loadURLContents);
    }


}