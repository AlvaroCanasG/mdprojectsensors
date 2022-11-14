package dte.masteriot.mdp.mdprojectsensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class SecondActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {
    Calendar calendar;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("H");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("M");
    SimpleDateFormat dateFormat3 = new SimpleDateFormat("s");
    
    Button bLight;
    Button bTemp;
    Button bHumidity;
    

    TextView tvSensorValue;
    TextView tvTempValue;
    TextView tvHumidityValue;



    private SensorManager sensorManager;
    private Sensor lightSensor;


    boolean humiditySensorIsActive;
    boolean temperatureSensorIsActive;
    boolean lightSensorIsActive;
    boolean GreenhouseSelected;

    String lightMeasurement = "0.0";
    String temperatureMeasurement = "0.0";
    String humidityMeasurement = "0.0";
    MQTTClient myMQTT;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myMQTT = new MQTTClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button bPublish = findViewById(R.id.bPublish);
        bPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!myMQTT.mqttAndroidClient.isConnected()){
                    myMQTT.SendNotification("No connection");
                } else if (!GreenhouseSelected) {
                            myMQTT.SendNotification("Please select a Greenhouse");
                        } else {
                                if(!(humiditySensorIsActive | temperatureSensorIsActive | lightSensorIsActive)){
                                    myMQTT.SendNotification("No sensor ON");
                                } else {
                                        myMQTT.publishTopic = myMQTT.publishTopic + "/Date";
                                        calendar = Calendar.getInstance();
                                        String date = dateFormat.format(calendar.getTime());
                                        try {
                                            myMQTT.publishMessage(date);
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                        }

                                        if (lightSensorIsActive) {
                                                myMQTT.publishTopic = myMQTT.publishTopic + "/Light";
                                                try {
                                                    myMQTT.publishMessage(lightMeasurement);
                                                } catch (MqttException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        if (temperatureSensorIsActive) {
                                                myMQTT.publishTopic = myMQTT.publishTopic + "/Temperature";
                                                try {
                                                    myMQTT.publishMessage(temperatureMeasurement);
                                                } catch (MqttException e) {
                                                   e.printStackTrace();
                                                }
                                        }
                                        if (humiditySensorIsActive) {
                                            myMQTT.publishTopic = myMQTT.publishTopic + "/Humidity";
                                            try {
                                                myMQTT.publishMessage(humidityMeasurement);
                                            } catch (MqttException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                }
                        }
                }
        });

        myMQTT.clientId = "id";
        myMQTT.mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), myMQTT.serverUri, myMQTT.clientId);
        myMQTT.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    myMQTT.SendNotification("Reconnected to : " + serverURI);
                } else {
                    myMQTT.SendNotification("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                myMQTT.SendNotification("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                myMQTT.SendNotification("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        //Last Will message
        mqttConnectOptions.setWill(myMQTT.publishTopic,myMQTT.LWillmessage.getBytes(),1,false);

        //SendNotification("Connecting to " + serverUri);
        try {
            myMQTT.mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    myMQTT.mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    myMQTT.SendNotification("Failed to connect to: " + myMQTT.serverUri);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        lightSensorIsActive = false;
        temperatureSensorIsActive = false;
        humiditySensorIsActive = false;
        GreenhouseSelected = false;


        // Get the references to the UI:
        bLight = findViewById(R.id.bLight); // button to start/stop sensor's readings
        tvSensorValue = findViewById(R.id.lightMeasurement); // sensor's values

        bTemp = findViewById(R.id.bTemp);
        tvTempValue = findViewById(R.id.tempMeasurement);


        bHumidity = findViewById(R.id.bHum);
        tvHumidityValue = findViewById(R.id.humMeasurement);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //MGM
        List<Item> listofitems = ((MyApplication) this.getApplication()).getListofitems();
        //List<Item> listofitems = new ArrayList<>()
        //ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_spinner_item , listofitems);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Greenhouses_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        // Get the reference to the sensor manager and the sensor:
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Obtain the reference to the default light sensor of the device:
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        

        // Listener for the button:
        bLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lightSensorIsActive) {
                    // unregister listener and make the appropriate changes in the UI:
                    sensorManager.unregisterListener(SecondActivity.this, lightSensor);
                    tvSensorValue.setText("- - -");
                    lightSensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI:
                    sensorManager.registerListener(SecondActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    tvSensorValue.setText("- - -");
                    lightSensorIsActive = true;
                }
            }
        });
        // Listener for the button2:
        bTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temperatureSensorIsActive) {
                    tvTempValue.setText("- - -");
                    temperatureSensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI
                    TemperatureEmulator();
                    tvTempValue.setText(temperatureMeasurement+"ºC");
                    temperatureSensorIsActive = true;
                }
            }
        });

        // Listener for the button3:
        bHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (humiditySensorIsActive) {
                    // unregister listener and make the appropriate changes in the UI:
                    tvHumidityValue.setText("- - -");
                    humiditySensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI:
                    HumidityEmulator();
                    tvHumidityValue.setText(humidityMeasurement+"%");
                    humiditySensorIsActive = true;
                }
            }
        });




    }




// to store the values of the variables that keep the status of each sensor
    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("lightSensorIsActive", lightSensorIsActive);
        editor.putBoolean("temperatureSensorIsActive", temperatureSensorIsActive);
        editor.putBoolean("stepSensorIsActive", humiditySensorIsActive);
        editor.apply();
    }

    


    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lightMeasurement = String.format("%.01f",sensorEvent.values[0]);
        tvSensorValue.setText(lightMeasurement);
    }

    
    public void TemperatureEmulator(){ //Emulates the temperature depending on the Hour and Month
        calendar = Calendar.getInstance();
        float month = Float.parseFloat(dateFormat2.format(calendar.getTime()));
        float aux = (float)(new Random().nextInt(11) - 1)/10;
        int base;
        if((month > 4.0) &(month <11)){
            base = 21;
        } else{
            base = 12;
        }
        float hour = Float.parseFloat(dateFormat1.format(calendar.getTime()));
        if((8.0 < hour) & (hour < 20.0) ){// Day-time hours
            base = base + new Random().nextInt(11) - new Random().nextInt(5) ;//[18- 30 ºC] and [9-21 ºC]

        } else {
            base = base + new Random().nextInt(4) - new Random().nextInt(7);//[ 16- 23ºC] and [ 7 and 14 ºC]
        }
        temperatureMeasurement = String.valueOf((float)base + aux);
    }

    @SuppressLint("DefaultLocale")
    public void HumidityEmulator(){
        calendar = Calendar.getInstance();
        float seconds = Float.parseFloat(dateFormat3.format(calendar.getTime()));
        humidityMeasurement = String.format("%.01f",(75.0 + seconds/10));
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        GreenhouseSelected = true;
        switch (i) {
            case 0:
                myMQTT.publishTopic = "Tomato";
                break;
            case 1:
                myMQTT.publishTopic = "Pepper";
                break;
            case 2:
                myMQTT.publishTopic = "Eggplant";
                break;
            case 3:
                myMQTT.publishTopic = "GreenBean";
                break;
            case 4:
                myMQTT.publishTopic = "Zucchini";
                break;
            case 5:
                myMQTT.publishTopic = "Cucumber";
                break;
            case 6:
                myMQTT.publishTopic = "Melon";
                break;
            case 7:
                myMQTT.publishTopic = "Watermelon";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
            GreenhouseSelected = false;
    }
}
