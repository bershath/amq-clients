package org.jboss.labs.amq.jms;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

public class MQTTSenderUpdated {
    public static void main(String[] args){
        System.out.println("Connecting to Artemis using MQTT");
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://localhost:1883");
            mqtt.setUserName("admin");
            mqtt.setPassword("jboss100");
            BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            System.out.println("Connected to Artemis");
            byte[] payload = new byte[131072];

            for(int i = 0; i < 4; i++ ){
                connection.publish("testTopic" + String.format("-%04d-x", i), payload, QoS.AT_LEAST_ONCE, false);
                connection.publish("testTopic" + String.format("-%04d-y", i), payload, QoS.AT_MOST_ONCE, false);
                connection.publish("testTopic" + String.format("-%04d-z", i), payload, QoS.AT_MOST_ONCE, false);
            }

            System.out.println("Sent messages.");
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
