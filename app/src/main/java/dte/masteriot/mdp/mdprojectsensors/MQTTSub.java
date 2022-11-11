package dte.masteriot.mdp.mdprojectsensors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MQTTSub implements Runnable{//This class executes a thread which creates one MQTT Client for each Greenhouse. Then each client subscribes to the corresponding topic
    Handler creator;                    //Also, a callback is set for each client, in order to handle the incoming messages
    Activity thisactivity;
    public MQTTSub(Handler handler, Activity activity) {
        creator = handler;
        thisactivity = activity;
    }


    @Override
    public void run() {

        //Create and initialize each message that will be sent from the back to the foreground

        Message msg;
        Bundle msg_data;
        msg = creator.obtainMessage();


        msg_data = msg.getData();


        //Creation and subscription of the MQTT client

        MQTTClient Greenhouse = new MQTTClient(thisactivity);
        Greenhouse.subscriptionTopic = "Tomato/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "Eggplant/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "Pepper/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "GreenBean/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "Cucumber/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }


        Greenhouse.subscriptionTopic = "Zucchini/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "Melon/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Greenhouse.subscriptionTopic = "Watermelon/#";
        try {
            Greenhouse.subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }


        //Callback set for each client
        Greenhouse.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception { //When an MQTT message arrives, it is sent to the foreground
                String measurement = new String(message.getPayload());
                msg_data.putString(topic,measurement);
                creator.sendMessage(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



    }
}
