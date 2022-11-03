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
import android.widget.Button;
import android.widget.RadioButton;
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
import java.util.Calendar;
import java.util.Random;

public class SecondActivity extends AppCompatActivity implements SensorEventListener {
    Calendar calendar;
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
                                if(!(humiditySensorIsActive | temperatureSensorIsActive | lightSensorIsActive)){
                                    myMQTT.SendNotification("No sensor ON");
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
                    bLight.setText(R.string.light_sensor_off);
                    bLight.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                    tvSensorValue.setText("Light sensor is OFF");
                    lightSensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI:
                    sensorManager.registerListener(SecondActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    bLight.setText(R.string.light_sensor_on);
                    bLight.setBackground(getResources().getDrawable(R.drawable.round_button_on));
                    tvSensorValue.setText("Waiting for first light sensor value");
                    lightSensorIsActive = true;
                }
            }
        });
        // Listener for the button2:
        bTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temperatureSensorIsActive) {
                    bTemp.setText("Temp OFF");
                    bTemp.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                    tvTempValue.setText("Temperature sensor is OFF");

                    temperatureSensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI
                    bTemp.setText("Temp ON");
                    bTemp.setBackground(getResources().getDrawable(R.drawable.round_button_on));
                    TemperatureEmulator();
                    tvTempValue.setText(temperatureMeasurement);
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
                    bHumidity.setText("Hum OFF");
                    bHumidity.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                    tvHumidityValue.setText("Humidity Sensor is OFF");
                    humiditySensorIsActive = false;
                } else {
                    // register listener and make the appropriate changes in the UI:
                    bHumidity.setText("Hum ON");
                    bHumidity.setBackground(getResources().getDrawable(R.drawable.round_button_on));
                    HumidityEmulator();
                    tvHumidityValue.setText(humidityMeasurement);
                    humiditySensorIsActive = true;
                }
            }
        });




    }
    public void OnRadioButtonClicked(View view) {
        // Is the button now checked?
         GreenhouseSelected = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.buttonA:
                if (GreenhouseSelected)
                    myMQTT.publishTopic = "GreenhouseA";
                break;
            case R.id.buttonB:
                if (GreenhouseSelected)
                    myMQTT.publishTopic = "GreenhouseB";
                break;

            case R.id.buttonC:
                if (GreenhouseSelected)
                    myMQTT.publishTopic = "GreenhouseC";
                break;

            case R.id.buttonD:
                if (GreenhouseSelected)
                    myMQTT.publishTopic = "GreenhouseD";
                break;
        }
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

}
