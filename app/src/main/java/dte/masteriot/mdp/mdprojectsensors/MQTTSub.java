package dte.masteriot.mdp.mdprojectsensors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MQTTSub implements Runnable{//This class executes a thread to handle the messages received from a specif MQTT client which is subscribed to different topics
    Handler creator;
    MQTTClient Greenhouse;
    public MQTTSub(Handler handler, MQTTClient MQTTclient) {
        this.creator = handler;
        this.Greenhouse = MQTTclient;
    }


    @Override
    public void run() {

        //Create and initialize each message that will be sent from the back to the foreground

        Message msg;
        Bundle msg_data;
        msg = creator.obtainMessage();
        msg_data = msg.getData();


        //Callback set for the client
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
                creator.dispatchMessage(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



    }
}
