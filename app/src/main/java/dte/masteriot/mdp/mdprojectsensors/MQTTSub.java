package dte.masteriot.mdp.mdprojectsensors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MQTTSub implements Runnable{
    Handler creator;
    Activity thisactivity;
    public MQTTSub(Handler handler, Activity activity) {
        creator = handler;
        thisactivity = activity;
    }


    @Override
    public void run() {
        Message msg_A;
        Bundle msg_dataA;
        msg_A = creator.obtainMessage();
        msg_dataA = msg_A.getData();
        MQTTClient Greenhouse_A = new MQTTClient(thisactivity);
        Greenhouse_A.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
