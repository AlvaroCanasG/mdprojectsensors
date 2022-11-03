/*
 Copyright (c) 1999, 2016 IBM Corp.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v1.0 which accompany this distribution.

 The Eclipse Public License is available at
 http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.

 */
package dte.masteriot.mdp.mdprojectsensors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MQTTClient extends AppCompatActivity {
    final String serverUri = "tcp://172.20.10.3:1883";
    String publishTopic = "topic";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";
    Calendar calendar;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMM HH:mm:ss");
    final String LWillmessage = "disconnected";
    public Activity activity;
    public MQTTClient(Activity _activity){
        this.activity = _activity;
    }

    public void SendNotification(String mainText) {

        Snackbar.make(this.activity.findViewById(R.id.bPublish),
                mainText,
                BaseTransientBottomBar.LENGTH_SHORT
        ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }


    public void publishMessage(String measurement) throws MqttException {
        calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());

        MqttMessage message = new MqttMessage();
        message.setPayload(measurement.getBytes());
        mqttAndroidClient.publish(publishTopic, message);
        SendNotification("Message Published");
        if (!mqttAndroidClient.isConnected()) {
            SendNotification("Not connected");
        }
    }
}
