package org.jboss.labs.amq;

import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisCFClient {

    public static final String userName = "admin";
    public static final String password = "jboss100";
    public static final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";
    public static final String queuePhysicalName = "A";

    public static void main(String[] args) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url,userName,password);
        try(JMSContext jmsContext = activeMQConnectionFactory.createContext()){
           Queue queue = jmsContext.createQueue(queuePhysicalName); //This would not create a new destination but offers a reference to an existing destination
           JMSProducer jmsProducer = jmsContext.createProducer();
           jmsProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
           jmsProducer.send(queue,jmsContext.createTextMessage("Artemis CF Client"));
           JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
            TextMessage receivedMessage = (TextMessage) jmsConsumer.receive(30000);
            System.out.println(receivedMessage.getBody(String.class));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
